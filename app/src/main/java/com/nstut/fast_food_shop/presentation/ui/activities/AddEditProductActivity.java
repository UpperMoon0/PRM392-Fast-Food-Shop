package com.nstut.fast_food_shop.presentation.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.data.local.dao.ProductDao;
import com.nstut.fast_food_shop.data.local.db.AppDatabase;
import com.nstut.fast_food_shop.data.models.Category;
import com.nstut.fast_food_shop.data.models.ProductRoom;
import com.nstut.fast_food_shop.data.models.ProductWithCategories;
import com.nstut.fast_food_shop.presentation.ui.adapters.CategorySelectionAdapter;
import com.nstut.fast_food_shop.presentation.utils.CloudinaryManager;
import com.nstut.fast_food_shop.presentation.utils.FileUtil;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class AddEditProductActivity extends BaseActivity {

    private EditText edtName, edtDesc, edtPrice;
    private RecyclerView recyclerViewCategories;
    private ImageView imageView;
    private Button btnChooseImage, btnSave;
    private Uri selectedImageUri;
    private ProductDao productDao;
    private String productId;
    private ProductWithCategories currentProduct;
    private String uploadedImageUrl = "";
    private List<Category> categoryList;
    private CategorySelectionAdapter categoryAdapter;
    private boolean isEditMode = false;
    private CloudinaryManager cloudinaryManager;

    private static final int REQUEST_IMAGE_PICK = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cloudinaryManager = new CloudinaryManager();
        setContentView(R.layout.activity_add_edit_product);
        
        edtName = findViewById(R.id.edtName);
        edtDesc = findViewById(R.id.edtDesc);
        edtPrice = findViewById(R.id.edtPrice);
        recyclerViewCategories = findViewById(R.id.recycler_view_categories);
        imageView = findViewById(R.id.imageView);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnSave = findViewById(R.id.btnSave);

        productDao = AppDatabase.getInstance(this).productDao();
        productId = getIntent().getStringExtra("product_id");
        isEditMode = productId != null;

        setupRecyclerView();
        loadCategories();

        if (isEditMode) {
            btnSave.setText("Save Changes");
        }

        btnChooseImage.setOnClickListener(v -> openImagePicker());

        btnSave.setOnClickListener(v -> saveProduct());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupHeader(findViewById(R.id.secondary_header));
    }

    private void setupRecyclerView() {
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this));
        categoryAdapter = new CategorySelectionAdapter();
        recyclerViewCategories.setAdapter(categoryAdapter);
    }

    private void loadCategories() {
        AppDatabase.getInstance(this).categoryDao().getAllCategories().observe(this, categories -> {
            categoryList = categories;
            categoryAdapter.setCategories(categoryList);
            if (isEditMode) {
                loadProductDetails();
            }
        });
    }

    private void loadProductDetails() {
        productDao.getProductWithCategories(Integer.parseInt(productId)).observe(this, product -> {
            currentProduct = product;
            if (currentProduct != null) {
                edtName.setText(currentProduct.product.getName());
                edtDesc.setText(currentProduct.product.getDescription());
                edtPrice.setText(String.valueOf(currentProduct.product.getPrice()));
                if (currentProduct.product.getImageUrl() != null && !currentProduct.product.getImageUrl().isEmpty()) {
                    Glide.with(this).load(currentProduct.product.getImageUrl()).into(imageView);
                }
                List<Integer> selectedIds = currentProduct.categories.stream()
                        .map(Category::getId)
                        .collect(Collectors.toList());
                categoryAdapter.setSelectedCategoryIds(selectedIds);
            }
        });
    }

    private void saveProduct() {
        String name = edtName.getText().toString();
        String description = edtDesc.getText().toString();
        double price = Double.parseDouble(edtPrice.getText().toString());

        if (selectedImageUri != null) {
            try {
                File imageFile = FileUtil.from(this, selectedImageUri);
                cloudinaryManager.uploadImage(imageFile, "project-prm", result -> {
                    uploadedImageUrl = result;
                    if (isEditMode) {
                        updateProduct(name, description, price, uploadedImageUrl);
                    } else {
                        createProduct(name, description, price, uploadedImageUrl);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Upload image failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (isEditMode) {
                updateProduct(name, description, price, currentProduct.product.getImageUrl());
            } else {
                createProduct(name, description, price, "");
            }
        }
    }

    private void createProduct(String name, String description, double price, String imageUrl) {
        Executors.newSingleThreadExecutor().execute(() -> {
            ProductRoom product = new ProductRoom();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setAvailable(true);
            String now = java.time.LocalDateTime.now().toString();
            product.setCreatedAt(now);
            product.setUpdatedAt(now);
            product.setImageUrl(imageUrl);

            List<Integer> selectedCategoryIds = categoryAdapter.getSelectedCategoryIds();
            productDao.insertProductWithCategories(product, selectedCategoryIds);

            runOnUiThread(() -> {
                Toast.makeText(this, "Product Saved", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void updateProduct(String name, String description, double price, String imageUrl) {
        Executors.newSingleThreadExecutor().execute(() -> {
            currentProduct.product.setName(name);
            currentProduct.product.setDescription(description);
            currentProduct.product.setPrice(price);
            if (imageUrl != null) {
                currentProduct.product.setImageUrl(imageUrl);
            }
            String now = java.time.LocalDateTime.now().toString();
            currentProduct.product.setUpdatedAt(now);

            List<Integer> selectedCategoryIds = categoryAdapter.getSelectedCategoryIds();
            productDao.updateProductWithCategories(currentProduct.product, selectedCategoryIds);

            runOnUiThread(() -> {
                Toast.makeText(this, "Product updated", Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_OK);
                finish();
            });
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);
        }
    }
}