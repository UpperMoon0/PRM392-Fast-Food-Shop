package com.nstut.fast_food_shop.presentation.ui.activities;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.util.Utils;
import com.nstut.fast_food_shop.data.local.db.AppDatabase;
import com.nstut.fast_food_shop.data.models.Category;
import com.nstut.fast_food_shop.data.models.ProductRoom;
import com.nstut.fast_food_shop.repository.CartRepository;

public class ProductDetailActivity extends BaseActivity {

    public static final String EXTRA_PRODUCT_ID = "extra_product_id";
    private CartRepository cartRepository;
    private ProductRoom currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        cartRepository = new CartRepository(this);

        ImageView productImageView = findViewById(R.id.product_image_view);
        TextView productNameTextView = findViewById(R.id.product_name_text_view);
        TextView productDescriptionTextView = findViewById(R.id.product_description_text_view);
        TextView productPriceTextView = findViewById(R.id.product_price_text_view);
        com.google.android.material.chip.ChipGroup categoryChipGroup = findViewById(R.id.category_chip_group);
        Button addToCartButton = findViewById(R.id.add_to_cart_button);
        

        int productId = getIntent().getIntExtra(EXTRA_PRODUCT_ID, -1);

        if (productId != -1) {
            AppDatabase.getInstance(this).productDao().getProductWithCategories(productId).observe(this, productWithCategories -> {
                if (productWithCategories != null) {
                    currentProduct = productWithCategories.product;
                    TextView appName = findViewById(R.id.app_name);
                    appName.setText(currentProduct.getName());
                    Glide.with(this).load(currentProduct.getImageUrl()).into(productImageView);
                    productNameTextView.setText(currentProduct.getName());
                    productDescriptionTextView.setText(currentProduct.getDescription());
                    productPriceTextView.setText(Utils.formatCurrency(currentProduct.getPrice()));

                    categoryChipGroup.removeAllViews();
                    for (Category category : productWithCategories.categories) {
                        com.google.android.material.chip.Chip chip = new com.google.android.material.chip.Chip(this);
                        chip.setText(category.getName());
                        categoryChipGroup.addView(chip);
                    }
                }
            });
        }


        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);

        if (isLoggedIn) {
            addToCartButton.setVisibility(View.VISIBLE);
        } else {
            addToCartButton.setVisibility(View.GONE);
        }

        addToCartButton.setOnClickListener(v -> {
            if (currentProduct != null) {
                cartRepository.addItemToCart(currentProduct, 1);
                Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
                updateCartBadge();
            }
        });

    }

    @Override
    public void setupHeader() {
        super.setupHeader();
        setupHeader(true);
    }
}