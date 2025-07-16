    package com.nstut.fast_food_shop;

    import android.os.Bundle;

    import androidx.appcompat.app.AppCompatActivity;
    import android.content.Intent;
    import android.view.View;
    import android.widget.TextView;

    import androidx.recyclerview.widget.LinearLayoutManager;
    import com.nstut.fast_food_shop.adapter.OrderAdapter;
    import com.nstut.fast_food_shop.databinding.ActivityOrderBinding;
    import com.nstut.fast_food_shop.model.Product;
    import com.nstut.fast_food_shop.repository.ProductRepository;
    
    import retrofit2.Call;
    import retrofit2.Callback;
    import retrofit2.Response;
    import com.nstut.fast_food_shop.util.Utils;

    import java.math.BigDecimal;
    import java.util.ArrayList;
    import java.util.List;
    
    public class OrderActivity extends AppCompatActivity {

        private ActivityOrderBinding binding;
        private final List<Product> cart = new ArrayList<>();
        private ProductRepository productRepository;

        private TextView badge;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivityOrderBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            badge = findViewById(R.id.cartBadge);
            productRepository = new ProductRepository();

            binding.rvMenu.setLayoutManager(new LinearLayoutManager(this));
            fetchProducts();

            binding.btnViewCart.setOnClickListener(v -> {
                Intent i = new Intent(this, CartActivity.class);
                i.putParcelableArrayListExtra("cart", new ArrayList<>(cart));
                startActivity(i);
            });
            binding.btnCheckout.setOnClickListener(v -> {
                if (cart.isEmpty()) return; // không có món thì không cho đi tiếp

                Intent i = new Intent(this, PaymentActivity.class);
                i.putParcelableArrayListExtra("cart", new ArrayList<>(cart));
                startActivity(i);
            });

        }

        private void updateCartBadge() {
            int totalQuantity = 0;
            for (Product item : cart) {
                totalQuantity += item.getQuantity();
            }

            if (totalQuantity > 0) {
                badge.setVisibility(View.VISIBLE);
                badge.setText(String.valueOf(totalQuantity));
            } else {
                badge.setVisibility(View.GONE);
            }
        }
        private void updateTotal() {
            BigDecimal total = BigDecimal.ZERO;
            for (Product item : cart) {
                total = total.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
            binding.tvCartTotal.setText("Tổng: " + Utils.formatCurrency(total));
        }

        private void fetchProducts() {
            productRepository.getAllProducts().enqueue(new Callback<List<Product>>() {
                @Override
                public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Product> products = response.body();
                        OrderAdapter adapter = new OrderAdapter(products, item -> {
                            boolean found = false;
                            for (Product p : cart) {
                                if (p.getId().equals(item.getId())) {
                                    p.setQuantity(item.getQuantity());
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                cart.add(item);
                            }
                            updateCartBadge();
                            updateTotal();
                        });
                        binding.rvMenu.setAdapter(adapter);
                    }
                }

                @Override
                public void onFailure(Call<List<Product>> call, Throwable t) {
                    // Handle failure
                }
            });
        }
    }

