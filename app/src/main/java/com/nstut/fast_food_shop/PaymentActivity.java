// PaymentActivity.java
package com.nstut.fast_food_shop;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nstut.fast_food_shop.adapter.CartAdapter;
import com.nstut.fast_food_shop.databinding.ActivityPaymentBinding;
import com.nstut.fast_food_shop.model.FoodItem;
import com.nstut.fast_food_shop.util.Utils;
import java.util.ArrayList;

public class PaymentActivity extends AppCompatActivity {

    private ActivityPaymentBinding binding;
    private ArrayList<FoodItem> cart;

    private static final int SHIPPING_FEE = 15000;
    private static final int OTHER_FEE = 6000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cart = getIntent().getParcelableArrayListExtra("cart");
        if (cart == null) cart = new ArrayList<>();

        CartAdapter adapter = new CartAdapter(cart, this::updateTotal);
        binding.rvOrderList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvOrderList.setAdapter(adapter);

        // Địa chỉ giả lập (sau này có thể lấy từ tài khoản)
        binding.tvAddress.setText("70 Trịnh Quang Nghị, P.4, Tân An");
        binding.tvPhone.setText("Tuấn Khang - 84849135986");

        binding.btnPlaceOrder.setOnClickListener(v -> {
            Toast.makeText(this, "Đặt đơn thành công!", Toast.LENGTH_SHORT).show();
            finish();
        });

        updateTotal();
    }

    private void updateTotal() {
        int itemTotal = 0;
        for (FoodItem item : cart) {
            itemTotal += item.getPrice() * item.getQuantity();
        }
        int finalTotal = itemTotal + SHIPPING_FEE + OTHER_FEE;

        binding.tvItemTotal.setText("Tổng giá món: " + Utils.formatCurrency(itemTotal));
        binding.tvShippingFee.setText("Phí giao hàng: " + Utils.formatCurrency(SHIPPING_FEE));
        binding.tvOtherFee.setText("Phí khác: " + Utils.formatCurrency(OTHER_FEE));
        binding.tvTotalPrice.setText("Tổng thanh toán: " + Utils.formatCurrency(finalTotal));
        binding.btnPlaceOrder.setText("Đặt đơn - " + Utils.formatCurrency(finalTotal));
    }
}
