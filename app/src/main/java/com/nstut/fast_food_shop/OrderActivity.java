package com.nstut.fast_food_shop;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nstut.fast_food_shop.adapter.OrderAdapter;
import com.nstut.fast_food_shop.databinding.ActivityOrderBinding;
import com.nstut.fast_food_shop.model.FoodItem;
import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {

    private ActivityOrderBinding binding;
    private final List<FoodItem> cart = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Fake data menu
        List<FoodItem> menu = List.of(
                new FoodItem("Burger", 45000, R.drawable.ic_burger),
                new FoodItem("Fries", 32000, R.drawable.ic_fries),
                new FoodItem("Coke", 15000, R.drawable.ic_coke)
        );

        OrderAdapter adapter = new OrderAdapter(menu, item -> {
            boolean found = false;
            for (FoodItem f : cart) {
                if (f.getName().equals(item.getName())) {
                    f.setQuantity(item.getQuantity()); // cập nhật số lượng
                    found = true;
                    break;
                }
            }
            if (!found) {
                cart.add(new FoodItem(item.getName(), item.getPrice(), item.getImageResId(), item.getQuantity())); // tạo bản sao
            }
            updateCartButton();
        });



        binding.rvMenu.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMenu.setAdapter(adapter);

        binding.btnViewCart.setOnClickListener(v -> {
            Intent i = new Intent(this, CartActivity.class);
            i.putParcelableArrayListExtra("cart", new ArrayList<>(cart));
            startActivity(i);
        });

    }

    private void updateCartButton() {
        binding.btnViewCart.setText("Xem giỏ hàng (" + cart.size() + " món)");
    }
}
