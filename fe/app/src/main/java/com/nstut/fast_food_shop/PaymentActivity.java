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
import com.nstut.fast_food_shop.data.local.db.AppDatabase;
import com.nstut.fast_food_shop.data.models.User;
import com.nstut.fast_food_shop.databinding.ActivityPaymentBinding;
import com.nstut.fast_food_shop.model.CartItem;
import com.nstut.fast_food_shop.model.Order;
import com.nstut.fast_food_shop.presentation.ui.activities.BaseActivity;
import com.nstut.fast_food_shop.presentation.ui.activities.HomeActivity;
import com.nstut.fast_food_shop.repository.CartRepository;
import com.nstut.fast_food_shop.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class PaymentActivity extends BaseActivity {

    private ActivityPaymentBinding binding;
    private List<CartItem> cartItems;
    private AppDatabase db;
    private ActivityResultLauncher<Intent> stripeLauncher;
    private double finalTotal = 0;
    private static final String TAG = "PaymentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupHeader(true);

        db = AppDatabase.getInstance(this);

        stripeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d(TAG, "Received result from Stripe WebView. Result code: " + result.getResultCode());

                    if (result.getResultCode() == RESULT_OK) {
                        Log.d(TAG, "Payment successful!");
                        Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show();
                        createOrder("Paid");
                    } else {
                        Log.d(TAG, "Payment failed or cancelled");
                        Intent data = result.getData();
                        if (data != null) {
                            String paymentStatus = data.getStringExtra("payment_status");
                            Log.d(TAG, "Payment status: " + paymentStatus);

                            if ("cancelled".equals(paymentStatus)) {
                                Toast.makeText(this, "Payment cancelled.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Payment failed.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Payment failed or cancelled.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        cartItems = getIntent().getParcelableArrayListExtra("cart");
        if (cartItems == null) cartItems = new ArrayList<>();

        CartAdapter adapter = new CartAdapter(cartItems, this::updateTotal);
        binding.rvOrderList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvOrderList.setAdapter(adapter);

        updateTotal();
    }

    private void updateTotal() {
        double itemTotal = 0;
        for (CartItem item : cartItems) {
            itemTotal += item.getProduct().getPrice() * item.getQuantity();
        }
        finalTotal = itemTotal;

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
            } else {
                Toast.makeText(this, "Please select a payment method.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startStripeCheckout(double amount) {
        String backendUrl = "http://10.0.2.2:3000";
        Log.d(TAG, "Starting Stripe checkout for amount: " + amount);

        try {
            if (amount <= 0) {
                Log.e(TAG, "Invalid amount: " + amount);
                Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> params = new HashMap<>();
            Log.d(TAG, "Created mutable HashMap");
            params.put("productName", "FastFood Order");
            Log.d(TAG, "Added productName to params");
            params.put("amount", (long)(amount * 100));
            Log.d(TAG, "Added amount to params: " + (long)(amount * 100));

            JSONObject jsonBody = new JSONObject(params);
            Log.d(TAG, "Converted Map to JSONObject: " + jsonBody.toString());

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    backendUrl + "/create-checkout-session",
                    jsonBody,
                    response -> {
                        try {
                            String checkoutUrl = response.getString("url");
                            Log.d(TAG, "Received checkout URL: " + checkoutUrl);

                            Intent intent = new Intent(this, StripeWebViewActivity.class);
                            intent.putExtra("checkoutUrl", checkoutUrl);
                            stripeLauncher.launch(intent);
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing Stripe response", e);
                            Toast.makeText(this, "Lỗi khi đọc phản hồi từ Stripe", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Log.e(TAG, "Stripe error: " + error.getMessage(), error);
                        Toast.makeText(this, "Lỗi khi tạo phiên thanh toán Stripe", Toast.LENGTH_SHORT).show();
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            Volley.newRequestQueue(this).add(request);
        } catch (Exception e) {
            Log.e(TAG, "Error creating Stripe payment", e);
            Toast.makeText(this, "Lỗi khi tạo thanh toán Stripe", Toast.LENGTH_SHORT).show();
        }
    }
    private void createOrder(String status) {
        Log.d(TAG, "Creating order with status: " + status);

        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userJson = sharedPreferences.getString("user", null);
        if (userJson == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new Gson().fromJson(userJson, User.class);
        String userId = String.valueOf(user.userId);

        String orderId = "ORDER_" + System.currentTimeMillis();
        Order order = new Order(orderId, userId, cartItems, finalTotal, new Date(), status);

        AppDatabase.databaseWriteExecutor.execute(() -> {
            db.orderDao().insertOrder(order);
            runOnUiThread(() -> {
                Log.d(TAG, "Order created successfully: " + orderId);
                Toast.makeText(PaymentActivity.this, "Order placed successfully!", Toast.LENGTH_SHORT).show();

                new CartRepository(this).clearCart();

                Intent intent = new Intent(PaymentActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("order_success", true);
                intent.putExtra("order_id", orderId);
                startActivity(intent);
                finish();
            });
        });
    }
}