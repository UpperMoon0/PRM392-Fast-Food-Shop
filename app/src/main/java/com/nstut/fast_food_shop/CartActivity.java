package com.nstut.fast_food_shop;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nstut.fast_food_shop.adapter.CartAdapter;
import com.nstut.fast_food_shop.databinding.ActivityCartBinding;
import com.nstut.fast_food_shop.model.CartItem;
import com.nstut.fast_food_shop.repository.CartRepository;
import com.nstut.fast_food_shop.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private ActivityCartBinding binding;
    private CartRepository cartRepository;
    private List<CartItem> cartItems;
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cartRepository = new CartRepository(this);
        cartItems = cartRepository.getCartItems();

        adapter = new CartAdapter(cartItems, this::updateTotal);
        binding.rvCart.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCart.setAdapter(adapter);

        updateTotal();
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnCheckout.setOnClickListener(v -> {
            if (cartItems.isEmpty()) return;

            Intent i = new Intent(this, PaymentActivity.class);
            // You might need to make CartItem Parcelable to pass it in an intent
            // For now, we'll just start the activity.
            startActivity(i);
        });
    }

    private void updateTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        binding.tvTotal.setText("Tổng tiền: " + Utils.formatCurrency(total));
        cartRepository.saveCartItems(cartItems);
    }
}
