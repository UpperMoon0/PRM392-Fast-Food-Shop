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
import com.nstut.fast_food_shop.repository.CategoryRepository;
import com.nstut.fast_food_shop.repository.ProductRepository;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;

    private static final int REQUEST_IMAGE_PICK = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_product);
        
        edtName = findViewById(R.id.edtName);
        edtDesc = findViewById(R.id.edtDesc);
        edtPrice = findViewById(R.id.edtPrice);
        recyclerViewCategories = findViewById(R.id.recycler_view_categories);
        imageView = findViewById(R.id.imageView);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnSave = findViewById(R.id.btnSave);

        productRepository = new ProductRepository();
        categoryRepository = new CategoryRepository();
        productId = getIntent().getStringExtra("product_id");
        isEditMode = productId != null;

        setupRecyclerView();
        loadCategories();

        if (isEditMode) {
            btnSave.setText("Save Changes");
            loadProductDetails();
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
        categoryRepository.getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    categoryList = response.body();
                    categoryAdapter.setCategories(categoryList);
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(AddEditProductActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProductDetails() {
        productRepository.getProductById(productId).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    currentProduct = response.body();
                    edtName.setText(currentProduct.getName());
                    edtDesc.setText(currentProduct.getDescription());
                    edtPrice.setText(String.valueOf(currentProduct.getPrice()));
                    Glide.with(AddEditProductActivity.this).load(currentProduct.getImageUrl()).into(imageView);
                    categoryAdapter.setSelectedCategories(currentProduct.getCategories().stream().map(Category::getId).collect(Collectors.toList()));
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(AddEditProductActivity.this, "Failed to load product details", Toast.LENGTH_SHORT).show();
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
                uploadImageToBackend(imageFile, name, description, price);
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

    private void uploadImageToBackend(File imageFile, String name, String description, double price) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);
        
        productRepository.uploadImage(body).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String imageUrl = response.body();
                    runOnUiThread(() -> {
                        uploadedImageUrl = imageUrl;
                        if (isEditMode) {
                            updateProduct(name, description, price, uploadedImageUrl);
                        } else {
                            createProduct(name, description, price, uploadedImageUrl);
                        }
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(AddEditProductActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
                runOnUiThread(() -> Toast.makeText(AddEditProductActivity.this, "Image upload failed: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void createProduct(String name, String description, double price, String imageUrl) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setImageUrl(imageUrl);
        product.setCategories(categoryAdapter.getSelectedCategories().stream().map(id -> {
            Category c = new Category();
            c.setId(id);
            return c;
        }).collect(Collectors.toList()));

        productRepository.createProduct(product).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddEditProductActivity.this, "Product created successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddEditProductActivity.this, "Failed to create product", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(AddEditProductActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProduct(String name, String description, double price, String imageUrl) {
        currentProduct.setName(name);
        currentProduct.setDescription(description);
        currentProduct.setPrice(price);
        currentProduct.setImageUrl(imageUrl);
        currentProduct.setCategories(categoryAdapter.getSelectedCategories().stream().map(id -> {
            Category c = new Category();
            c.setId(id);
            return c;
        }).collect(Collectors.toList()));

        productRepository.updateProduct(productId, currentProduct).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddEditProductActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddEditProductActivity.this, "Failed to update product", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(AddEditProductActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
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