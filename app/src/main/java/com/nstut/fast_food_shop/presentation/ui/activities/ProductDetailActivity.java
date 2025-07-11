package com.nstut.fast_food_shop.presentation.ui.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.data.local.db.AppDatabase;
import com.nstut.fast_food_shop.data.models.Category;
import com.nstut.fast_food_shop.data.models.ProductWithCategories;
import com.nstut.fast_food_shop.data.models.ProductRoom;

import java.util.stream.Collectors;

public class ProductDetailActivity extends BaseActivity {

    public static final String EXTRA_PRODUCT_ID = "extra_product_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        ImageView productImageView = findViewById(R.id.product_image_view);
        TextView productNameTextView = findViewById(R.id.product_name_text_view);
        TextView productDescriptionTextView = findViewById(R.id.product_description_text_view);
        TextView productPriceTextView = findViewById(R.id.product_price_text_view);
        TextView productCategoriesTextView = findViewById(R.id.product_categories_text_view);
        Button addToCartButton = findViewById(R.id.add_to_cart_button);
        ImageView backButton = findViewById(R.id.back_button);

        int productId = getIntent().getIntExtra(EXTRA_PRODUCT_ID, -1);

        if (productId != -1) {
            AppDatabase.getInstance(this).productDao().getProductWithCategories(productId).observe(this, productWithCategories -> {
                if (productWithCategories != null) {
                    ProductRoom product = productWithCategories.product;
                    TextView appName = findViewById(R.id.app_name);
                    appName.setText(product.getName());
                    Glide.with(this).load(product.getImageUrl()).into(productImageView);
                    productNameTextView.setText(product.getName());
                    productDescriptionTextView.setText(product.getDescription());
                    productPriceTextView.setText(String.format("$%.2f", product.getPrice()));

                    String categories = productWithCategories.categories.stream().map(Category::getName).collect(Collectors.joining(", "));
                    productCategoriesTextView.setText(categories);
                }
            });
        }


        addToCartButton.setOnClickListener(v -> {
            Toast.makeText(this, "Add to cart clicked", Toast.LENGTH_SHORT).show();
        });

        backButton.setOnClickListener(v -> onBackPressed());
    }

    @Override
    public void setupHeader() {
        super.setupHeader();
    }
}