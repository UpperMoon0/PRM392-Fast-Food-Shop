package com.nstut.fast_food_shop;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nstut.fast_food_shop.adapter.CartAdapter;
import com.nstut.fast_food_shop.databinding.ActivityCartBinding;
import com.nstut.fast_food_shop.model.CartItem;
import com.nstut.fast_food_shop.presentation.ui.activities.BaseActivity;
import com.nstut.fast_food_shop.repository.CartRepository;
import com.nstut.fast_food_shop.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends BaseActivity {

    private ActivityCartBinding binding;
    private CartRepository cartRepository;
    private List<CartItem> cartItems;
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupHeader(true);

        cartRepository = new CartRepository(this);
        cartItems = cartRepository.getCartItems();

        adapter = new CartAdapter(cartItems, this::updateTotal);
        binding.rvCart.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCart.setAdapter(adapter);

        updateTotal();

        binding.btnCheckout.setOnClickListener(v -> {
            if (cartItems.isEmpty()) return;

            Intent i = new Intent(this, PaymentActivity.class);
            i.putParcelableArrayListExtra("cart", new ArrayList<>(cartItems));
            startActivity(i);
        });
    }

    private void updateTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        binding.tvTotal.setText("Total: " + Utils.formatCurrency(total));
        cartRepository.saveCartItems(cartItems);
    }
}
