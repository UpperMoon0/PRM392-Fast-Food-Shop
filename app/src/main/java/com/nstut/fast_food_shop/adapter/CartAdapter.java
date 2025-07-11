package com.nstut.fast_food_shop.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.model.FoodItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    public interface OnQuantityChanged {
        void onChanged();
    }

    private final List<FoodItem> cartItems;
    private final OnQuantityChanged listener;

    public CartAdapter(List<FoodItem> items, OnQuantityChanged listener) {
        this.cartItems = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItem item = cartItems.get(position);
        holder.name.setText(item.getName());
        holder.price.setText(item.getPrice() * item.getQuantity() + "đ");
        holder.quantity.setText(String.valueOf(item.getQuantity()));

        holder.btnMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(position);
                listener.onChanged();
            }
        });

        holder.btnPlus.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(position);
            listener.onChanged();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, quantity;
        Button btnPlus, btnMinus;

        ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.tvName);
            price = v.findViewById(R.id.tvPrice);
            quantity = v.findViewById(R.id.tvQuantity);
            btnPlus = v.findViewById(R.id.btnPlus);
            btnMinus = v.findViewById(R.id.btnMinus);
        }
    }
}
