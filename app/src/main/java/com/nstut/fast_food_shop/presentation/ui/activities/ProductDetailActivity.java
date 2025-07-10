package com.nstut.fast_food_shop.presentation.ui.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.data.models.ProductRoom;

public class ProductDetailActivity extends BaseActivity {

    public static final String EXTRA_PRODUCT = "extra_product";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        ImageView productImageView = findViewById(R.id.product_image_view);
        TextView productNameTextView = findViewById(R.id.product_name_text_view);
        TextView productDescriptionTextView = findViewById(R.id.product_description_text_view);
        TextView productPriceTextView = findViewById(R.id.product_price_text_view);

        ProductRoom product = (ProductRoom) getIntent().getSerializableExtra(EXTRA_PRODUCT);

        if (product != null) {
            Glide.with(this).load(product.getImageUrl()).into(productImageView);
            productNameTextView.setText(product.getName());
            productDescriptionTextView.setText(product.getDescription());
            productPriceTextView.setText(String.valueOf(product.getPrice()));
        }
    }
}