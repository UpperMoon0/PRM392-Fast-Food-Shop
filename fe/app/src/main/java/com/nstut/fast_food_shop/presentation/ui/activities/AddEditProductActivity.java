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
import com.nstut.fast_food_shop.data.models.Category;
import com.nstut.fast_food_shop.model.Product;
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
    private String productId;
    private Product currentProduct;
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

        // TODO: Get product repository instance
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
        setupHeader(findViewById(R.id.secondary_header), true);
    }

    private void setupRecyclerView() {
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this));
        categoryAdapter = new CategorySelectionAdapter();
        recyclerViewCategories.setAdapter(categoryAdapter);
    }

    private void loadCategories() {
        // TODO: Load categories from repository
    }

    private void loadProductDetails() {
        // TODO: Load product details from repository
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
        // TODO: Create product using repository
    }

    private void updateProduct(String name, String description, double price, String imageUrl) {
        // TODO: Update product using repository
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