package com.nstut.fast_food_shop.presentation.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.data.local.db.AppDatabase;
import com.nstut.fast_food_shop.data.models.Category;
import com.nstut.fast_food_shop.presentation.utils.CloudinaryManager;
import com.nstut.fast_food_shop.presentation.utils.FileUtil;

public class AddEditCategoryActivity extends BaseActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextName, editTextDescription;
    private ImageView imageView;
    private Button buttonSelectImage, buttonSave;
    private Uri imageUri;
    private AppDatabase appDatabase;
    private Category currentCategory;
    private int categoryId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_category);

        appDatabase = AppDatabase.getInstance(this);

        editTextName = findViewById(R.id.edit_text_category_name);
        editTextDescription = findViewById(R.id.edit_text_category_description);
        imageView = findViewById(R.id.image_view_category);
        buttonSelectImage = findViewById(R.id.button_select_image);
        buttonSave = findViewById(R.id.button_save_category);

        if (getIntent().hasExtra("category_id")) {
            categoryId = getIntent().getIntExtra("category_id", -1);
            loadCategory();
        }

        buttonSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        buttonSave.setOnClickListener(v -> saveCategory());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupHeader(findViewById(R.id.secondary_header));
    }

    private void loadCategory() {
        appDatabase.categoryDao().getCategoryById(categoryId).observe(this, category -> {
            currentCategory = category;
            if (currentCategory != null) {
                editTextName.setText(currentCategory.getName());
                editTextDescription.setText(currentCategory.getDescription());
                if (currentCategory.getImageUrl() != null && !currentCategory.getImageUrl().isEmpty()) {
                    Glide.with(this).load(currentCategory.getImageUrl()).into(imageView);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void saveCategory() {
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            try {
                File imageFile = FileUtil.from(this, imageUri);
                new CloudinaryManager().uploadImage(imageFile, "categories", imageUrl -> {
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        saveCategoryToDb(name, description, imageUrl);
                    } else {
                        Toast.makeText(AddEditCategoryActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
            }
        } else if (currentCategory != null) {
            saveCategoryToDb(name, description, currentCategory.getImageUrl());
        } else {
            saveCategoryToDb(name, description, null);
        }
    }

    private void saveCategoryToDb(String name, String description, String imageUrl) {
        new Thread(() -> {
            if (currentCategory == null) {
                currentCategory = new Category(name, description, imageUrl);
                appDatabase.categoryDao().insert(currentCategory);
            } else {
                currentCategory = new Category(name, description, imageUrl);
                currentCategory.setId(categoryId);
                appDatabase.categoryDao().update(currentCategory);
            }
            runOnUiThread(() -> {
                Toast.makeText(this, "Category saved", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}