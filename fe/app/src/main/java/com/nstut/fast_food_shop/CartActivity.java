package com.nstut.fast_food_shop;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nstut.fast_food_shop.adapter.CartAdapter;
import com.nstut.fast_food_shop.databinding.ActivityCartBinding;
import com.nstut.fast_food_shop.model.Cart;
import com.nstut.fast_food_shop.model.CartItem;
import com.nstut.fast_food_shop.presentation.ui.activities.BaseActivity;
import com.nstut.fast_food_shop.repository.CartRepository;
import com.nstut.fast_food_shop.model.User;
import com.nstut.fast_food_shop.util.Utils;

import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends BaseActivity {

    private ActivityCartBinding binding;
    private CartRepository cartRepository;
    private List<CartItem> cartItems = new ArrayList<>();
    private CartAdapter adapter;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupHeader(true);

        cartRepository = new CartRepository(this);
        currentUser = getCurrentUser();

        adapter = new CartAdapter(cartItems, new CartAdapter.OnQuantityChanged() {
            @Override
            public void onQuantityChanged(CartItem item, int quantity) {
                updateCartItem(item, quantity);
            }

            @Override
            public void onRemoveItem(CartItem item) {
                removeCartItem(item);
            }
        });
        binding.rvCart.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCart.setAdapter(adapter);

        if (currentUser != null) {
            fetchCart();
        }

        binding.btnCheckout.setOnClickListener(v -> {
            if (cartItems.isEmpty()) return;

            Intent i = new Intent(this, PaymentActivity.class);
            i.putParcelableArrayListExtra("cart", new ArrayList<>(cartItems));
            startActivity(i);
        });
    }

    private void fetchCart() {
        cartRepository.getCartByUserId(currentUser.getId()).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cartItems.clear();
                    cartItems.addAll(response.body().getItems());
                    adapter.notifyDataSetChanged();
                    updateTotal();
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void updateCartItem(CartItem item, int quantity) {
        cartRepository.updateCartItem(currentUser.getId(), item.getId(), quantity).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cartItems.clear();
                    cartItems.addAll(response.body().getItems());
                    adapter.notifyDataSetChanged();
                    updateTotal();
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void removeCartItem(CartItem item) {
        cartRepository.removeItemFromCart(currentUser.getId(), item.getId()).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cartItems.clear();
                    cartItems.addAll(response.body().getItems());
                    adapter.notifyDataSetChanged();
                    updateTotal();
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private void updateTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getProduct().getPrice().doubleValue() * item.getQuantity();
        }
        binding.tvTotal.setText("Total: " + Utils.formatCurrency(total));
    }
}
