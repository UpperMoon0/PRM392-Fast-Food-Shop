package com.nstut.fast_food_shop.presentation.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.data.local.db.AppDatabase;
import com.nstut.fast_food_shop.data.models.ProductRoom;
import com.nstut.fast_food_shop.presentation.utils.CloudinaryManager;
import com.nstut.fast_food_shop.presentation.utils.FileUtil;

import java.io.File;

public class AddProductActivity extends AppCompatActivity {
    EditText edtName, edtDesc, edtPrice, edtCategory;
    ImageView imageView;
    Button btnChooseImage, btnSave;
    Uri selectedImageUri;
    String uploadedImageUrl = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        edtName = findViewById(R.id.edtName);
        edtDesc = findViewById(R.id.edtDesc);
        edtPrice = findViewById(R.id.edtPrice);
        edtCategory = findViewById(R.id.edtCategory);
        imageView = findViewById(R.id.imageView);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnSave = findViewById(R.id.btnSave);

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
                product.setCategory(edtCategory.getText().toString());
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
