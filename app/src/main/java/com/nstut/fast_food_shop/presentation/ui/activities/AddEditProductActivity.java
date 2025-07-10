package com.nstut.fast_food_shop.presentation.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.data.local.dao.ProductDao;
import com.nstut.fast_food_shop.data.local.db.AppDatabase;
import com.nstut.fast_food_shop.data.models.Category;
import com.nstut.fast_food_shop.data.models.ProductRoom;
import com.nstut.fast_food_shop.presentation.utils.CloudinaryManager;
import com.nstut.fast_food_shop.presentation.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class AddEditProductActivity extends AppCompatActivity {

    private EditText edtName, edtDesc, edtPrice;
    private Spinner spinnerCategory;
    private ImageView imageView;
    private Button btnChooseImage, btnSave;
    private Uri selectedImageUri;
    private ProductDao productDao;
    private int productId;
    private ProductRoom currentProduct;
    private String uploadedImageUrl = "";
    private List<Category> categoryList;
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
        spinnerCategory = findViewById(R.id.spinnerCategory);
        imageView = findViewById(R.id.imageView);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnSave = findViewById(R.id.btnSave);

        productDao = AppDatabase.getInstance(this).productDao();

        productId = getIntent().getIntExtra("product_id", -1);
        isEditMode = productId != -1;

        loadCategories();

        if (isEditMode) {
            btnSave.setText("Save Changes");
            loadProductDetails();
        }

        btnChooseImage.setOnClickListener(v -> openImagePicker());

        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void loadCategories() {
        Executors.newSingleThreadExecutor().execute(() -> {
            categoryList = AppDatabase.getInstance(this).categoryDao().getAllCategories();
            runOnUiThread(() -> {
                List<String> categoryNames = categoryList.stream().map(Category::getName).collect(Collectors.toList());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(adapter);

                if (isEditMode && currentProduct != null) {
                    setSpinnerSelection();
                }
            });
        });
    }

    private void loadProductDetails() {
        Executors.newSingleThreadExecutor().execute(() -> {
            currentProduct = productDao.getById(productId);
            runOnUiThread(() -> {
                edtName.setText(currentProduct.getName());
                edtDesc.setText(currentProduct.getDescription());
                edtPrice.setText(String.valueOf(currentProduct.getPrice()));
                if (currentProduct.getImageUrl() != null && !currentProduct.getImageUrl().isEmpty()) {
                    Glide.with(this).load(currentProduct.getImageUrl()).into(imageView);
                }
                if (categoryList != null) {
                    setSpinnerSelection();
                }
            });
        });
    }

    private void setSpinnerSelection() {
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getId() == currentProduct.getCategoryId()) {
                spinnerCategory.setSelection(i);
                break;
            }
        }
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
                updateProduct(name, description, price, currentProduct.getImageUrl());
            } else {
                createProduct(name, description, price, "");
            }
        }
    }

    private void createProduct(String name, String description, double price, String imageUrl) {
        ProductRoom product = new ProductRoom();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        int selectedCategoryPosition = spinnerCategory.getSelectedItemPosition();
        if (selectedCategoryPosition >= 0 && selectedCategoryPosition < categoryList.size()) {
            product.setCategoryId(categoryList.get(selectedCategoryPosition).getId());
        }
        product.setAvailable(true);
        String now = java.time.LocalDateTime.now().toString();
        product.setCreatedAt(now);
        product.setUpdatedAt(now);
        product.setImageUrl(imageUrl);

        Executors.newSingleThreadExecutor().execute(() -> {
            productDao.insert(product);
            runOnUiThread(() -> {
                Toast.makeText(this, "Product Saved", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void updateProduct(String name, String description, double price, String imageUrl) {
        currentProduct.setName(name);
        currentProduct.setDescription(description);
        currentProduct.setPrice(price);
        int selectedCategoryPosition = spinnerCategory.getSelectedItemPosition();
        if (selectedCategoryPosition >= 0 && selectedCategoryPosition < categoryList.size()) {
            currentProduct.setCategoryId(categoryList.get(selectedCategoryPosition).getId());
        }
        if (imageUrl != null) {
            currentProduct.setImageUrl(imageUrl);
        }
        String now = java.time.LocalDateTime.now().toString();
        currentProduct.setUpdatedAt(now);

        Executors.newSingleThreadExecutor().execute(() -> {
            productDao.update(currentProduct);
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