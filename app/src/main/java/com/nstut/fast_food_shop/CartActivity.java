package com.nstut.fast_food_shop;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nstut.fast_food_shop.adapter.CartAdapter;
import com.nstut.fast_food_shop.databinding.ActivityCartBinding;
import com.nstut.fast_food_shop.model.FoodItem;
import com.nstut.fast_food_shop.util.Utils;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private ActivityCartBinding binding;
    private ArrayList<FoodItem> cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cart = getIntent().getParcelableArrayListExtra("cart");
        if (cart == null) cart = new ArrayList<>();

        CartAdapter adapter = new CartAdapter(cart, this::updateTotal);
        binding.rvCart.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCart.setAdapter(adapter);

        updateTotal();
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnCheckout.setOnClickListener(v -> {
            Intent i = new Intent(this, PaymentActivity.class);
            i.putParcelableArrayListExtra("cart", cart);
            startActivity(i);
        });
        binding.btnCheckout.setOnClickListener(v -> {
            if (cart.isEmpty()) return; // không có món thì không cho đi tiếp

            Intent i = new Intent(this, PaymentActivity.class);
            i.putParcelableArrayListExtra("cart", new ArrayList<>(cart));
            startActivity(i);
        });

    }

    private void updateTotal() {
        int total = 0;
        for (FoodItem item : cart) {
            total += item.getPrice() * item.getQuantity();
        }
        binding.tvTotal.setText("Tổng tiền: " + Utils.formatCurrency(total));
    }
}
