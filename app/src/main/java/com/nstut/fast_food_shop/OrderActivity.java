    package com.nstut.fast_food_shop;

    import android.os.Bundle;

    import androidx.appcompat.app.AppCompatActivity;
    import android.content.Intent;
    import android.view.View;
    import android.widget.TextView;

    import androidx.recyclerview.widget.LinearLayoutManager;
    import com.nstut.fast_food_shop.adapter.OrderAdapter;
    import com.nstut.fast_food_shop.databinding.ActivityOrderBinding;
    import com.nstut.fast_food_shop.model.FoodItem;
    import com.nstut.fast_food_shop.util.Utils;

    import java.util.ArrayList;
    import java.util.List;

    public class OrderActivity extends AppCompatActivity {

        private ActivityOrderBinding binding;
        private final List<FoodItem> cart = new ArrayList<>();

        private TextView badge;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivityOrderBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            badge = findViewById(R.id.cartBadge);

            List<FoodItem> menu = List.of(
                    new FoodItem("Burger", 45000, R.drawable.ic_food),
                    new FoodItem("Fries", 32000, R.drawable.ic_food),
                    new FoodItem("Coke", 15000, R.drawable.ic_food)
            );

            OrderAdapter adapter = new OrderAdapter(menu, item -> {
                boolean found = false;
                for (FoodItem f : cart) {
                    if (f.getName().equals(item.getName())) {
                        f.setQuantity(item.getQuantity());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    cart.add(new FoodItem(item.getName(), item.getPrice(), item.getImageResId(), item.getQuantity()));
                }
                updateCartBadge();
                updateTotal();
            });

            binding.rvMenu.setLayoutManager(new LinearLayoutManager(this));
            binding.rvMenu.setAdapter(adapter);

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
            for (FoodItem item : cart) {
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
            int total = 0;
            for (FoodItem item : cart) {
                total += item.getPrice() * item.getQuantity();
            }
            binding.tvCartTotal.setText("Tổng: " + Utils.formatCurrency(total));
        }


    }

