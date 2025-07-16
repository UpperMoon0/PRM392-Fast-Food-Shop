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
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.model.Category;
import com.nstut.fast_food_shop.presentation.utils.FileUtil;
import com.nstut.fast_food_shop.data.remote.ApiClient;
import com.nstut.fast_food_shop.data.remote.ApiService;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditCategoryActivity extends BaseActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextName, editTextDescription;
    private ImageView imageView;
    private Button buttonSelectImage, buttonSave;
    private Uri imageUri;
    private Category currentCategory;
    private String categoryId = null;
    private ExecutorService executorService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_category);

        executorService = Executors.newSingleThreadExecutor();

        editTextName = findViewById(R.id.edit_text_category_name);
        editTextDescription = findViewById(R.id.edit_text_category_description);
        imageView = findViewById(R.id.image_view_category);
        buttonSelectImage = findViewById(R.id.button_select_image);
        buttonSave = findViewById(R.id.button_save_category);

        if (getIntent().hasExtra("category_id")) {
            categoryId = getIntent().getStringExtra("category_id");
            loadCategory();
            buttonSave.setText("Save Changes");
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
        setupHeader(findViewById(R.id.secondary_header), true);
    }

    private void loadCategory() {
        // TODO: Call CategoryRepository to get category by ID
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
            String filePath = FileUtil.getPathFromUri(this, imageUri);
            if (filePath != null) {
                File imageFile = new File(filePath);
                uploadImageToBackend(imageFile, name, description);
            } else {
                Toast.makeText(this, "Failed to get file path from Uri", Toast.LENGTH_SHORT).show();
            }
        } else if (currentCategory != null) {
            saveCategoryToDb(name, description, currentCategory.getImageUrl());
        } else {
            saveCategoryToDb(name, description, null);
        }
    }

    private void uploadImageToBackend(File imageFile, String name, String description) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);
        
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<String> call = apiService.uploadImage(body);
        
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String imageUrl = response.body();
                    runOnUiThread(() -> saveCategoryToDb(name, description, imageUrl));
                } else {
                    runOnUiThread(() -> Toast.makeText(AddEditCategoryActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
                runOnUiThread(() -> Toast.makeText(AddEditCategoryActivity.this, "Image upload failed: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void saveCategoryToDb(String name, String description, String imageUrl) {
        // TODO: Call CategoryRepository to save the category
        Toast.makeText(this, "Category saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}