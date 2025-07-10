package com.nstut.fast_food_shop.presentation.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.data.local.db.AppDatabase;
import com.nstut.fast_food_shop.data.models.Category;
import com.nstut.fast_food_shop.data.models.ProductRoom;
import com.nstut.fast_food_shop.presentation.utils.CloudinaryManager;
import com.nstut.fast_food_shop.presentation.utils.FileUtil;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class AddProductActivity extends AppCompatActivity {
    EditText edtName, edtDesc, edtPrice;
    Spinner spinnerCategory;
    ImageView imageView;
    Button btnChooseImage, btnSave;
    Uri selectedImageUri;
    String uploadedImageUrl = "";
    private List<Category> categoryList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        edtName = findViewById(R.id.edtName);
        edtDesc = findViewById(R.id.edtDesc);
        edtPrice = findViewById(R.id.edtPrice);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        imageView = findViewById(R.id.imageView);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnSave = findViewById(R.id.btnSave);

        loadCategories();

        btnChooseImage.setOnClickListener(v -> openGallery());

        btnSave.setOnClickListener(v -> {
            saveProduct();
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 123);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        if (reqCode == 123 && resCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);
        }
    }

    private void loadCategories() {
        new Thread(() -> {
            categoryList = AppDatabase.getInstance(this).categoryDao().getAllCategories();
            runOnUiThread(() -> {
                List<String> categoryNames = categoryList.stream().map(Category::getName).collect(Collectors.toList());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(adapter);
            });
        }).start();
    }

    private void saveProduct() {
        new Thread(() -> {
            try {
                if (selectedImageUri != null) {
                    CloudinaryManager cloudinaryManager = new CloudinaryManager();
                    File file = FileUtil.from(this, selectedImageUri);
                    uploadedImageUrl = cloudinaryManager.uploadImageToFolder(file, "project-prm");
                }

                ProductRoom product = new ProductRoom();
                product.setName(edtName.getText().toString());
                product.setDescription(edtDesc.getText().toString());
                product.setPrice(Double.parseDouble(edtPrice.getText().toString()));
                int selectedCategoryPosition = spinnerCategory.getSelectedItemPosition();
                if (selectedCategoryPosition >= 0 && selectedCategoryPosition < categoryList.size()) {
                    product.setCategoryId(categoryList.get(selectedCategoryPosition).getId());
                }
                product.setAvailable(true); // Always true when adding
                String now = java.time.LocalDateTime.now().toString();
                product.setCreatedAt(now);
                product.setUpdatedAt(now);
                product.setImageUrl(uploadedImageUrl);

                AppDatabase.getInstance(this).productDao().insert(product);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Product Saved", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Failed to save product", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}
