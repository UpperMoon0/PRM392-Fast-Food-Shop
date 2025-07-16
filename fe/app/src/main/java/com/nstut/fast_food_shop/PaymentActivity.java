package com.nstut.fast_food_shop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.nstut.fast_food_shop.adapter.CartAdapter;
import com.nstut.fast_food_shop.databinding.ActivityPaymentBinding;
import com.nstut.fast_food_shop.model.CartItem;
import com.nstut.fast_food_shop.model.Order;
import com.nstut.fast_food_shop.presentation.ui.activities.BaseActivity;
import com.nstut.fast_food_shop.presentation.ui.activities.HomeActivity;
import com.nstut.fast_food_shop.repository.OrderRepository;
import com.nstut.fast_food_shop.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.*;

public class PaymentActivity extends BaseActivity implements CartAdapter.OnQuantityChanged {

    private ActivityPaymentBinding binding;
    private List<CartItem> cartItems;
    private ActivityResultLauncher<Intent> stripeLauncher;
    private OrderRepository orderRepository;


    private double finalTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupHeader(true);
        orderRepository = new OrderRepository();

        stripeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        createOrder("Paid");
                    } else {
                        Toast.makeText(this, "Payment failed or cancelled.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        cartItems = getIntent().getParcelableArrayListExtra("cart");
        if (cartItems == null) cartItems = new ArrayList<>();

        CartAdapter adapter = new CartAdapter(cartItems, this);
        binding.rvOrderList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvOrderList.setAdapter(adapter);

        updateTotal();
    }

    private void updateTotal() {
        BigDecimal itemTotal = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            itemTotal = itemTotal.add(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        finalTotal = itemTotal.doubleValue();

        binding.tvItemTotal.setVisibility(View.GONE);
        binding.tvShippingFee.setVisibility(View.GONE);
        binding.tvOtherFee.setVisibility(View.GONE);
        binding.tvTotalPrice.setText("Total Price: $" + String.format(Locale.getDefault(), "%,.2f", finalTotal));

        binding.btnPlaceOrder.setOnClickListener(v -> {
            String address = binding.edtAddress.getText().toString().trim();
            if (address.isEmpty()) {
                Toast.makeText(this, "Please enter a delivery address.", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedId = binding.radioGroupPayment.getCheckedRadioButtonId();
            if (selectedId == R.id.radioCod) {
                createOrder("Unpaid");
            } else if (selectedId == R.id.radioStripe) {
                startStripeCheckout(finalTotal);
            }
        });
    }

    private void startStripeCheckout(double amount) {
        String backendUrl = "https://46a0def34127.ngrok-free.app";
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("productName", "FastFood Order");
            jsonBody.put("amount", amount);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    backendUrl + "/create-checkout-session",
                    jsonBody,
                    response -> {
                        try {
                            String checkoutUrl = response.getString("url");
                            Intent intent = new Intent(this, StripeWebViewActivity.class);
                            intent.putExtra("checkoutUrl", checkoutUrl);
                            stripeLauncher.launch(intent);
                        } catch (JSONException e) {
                            Toast.makeText(this, "Error reading Stripe response", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(this, "Error creating Stripe session", Toast.LENGTH_SHORT).show()
            );

            Volley.newRequestQueue(this).add(request);
        } catch (Exception e) {
            Toast.makeText(this, "Error creating Stripe payment", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onQuantityChanged(CartItem item, int quantity) {
        updateTotal();
    }

    @Override
    public void onRemoveItem(CartItem item) {
        cartItems.remove(item);
        updateTotal();
    }

    private void createOrder(String status) {
        com.nstut.fast_food_shop.model.User user = getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = user.getId();

        Order order = new Order();
        order.setUserId(userId);
        order.setItems(cartItems);
        order.setTotalAmount(finalTotal);
        order.setOrderDate(new Date());
        order.setStatus(status);

        orderRepository.createOrder(order).enqueue(new retrofit2.Callback<Order>() {
            @Override
            public void onResponse(retrofit2.Call<Order> call, retrofit2.Response<Order> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PaymentActivity.this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                    // You might want to clear the cart here
                    Intent intent = new Intent(PaymentActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(PaymentActivity.this, "Failed to place order.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Order> call, Throwable t) {
                Toast.makeText(PaymentActivity.this, "Failed to place order.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}