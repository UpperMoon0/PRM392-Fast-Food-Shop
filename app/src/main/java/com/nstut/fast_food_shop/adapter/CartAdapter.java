package com.nstut.fast_food_shop.adapter;

import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.model.CartItem;
import com.nstut.fast_food_shop.util.Utils;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    public interface OnQuantityChanged {
        void onChanged();
    }
    private final List<CartItem> cartItems;
    private final OnQuantityChanged listener;

    public CartAdapter(List<CartItem> items, OnQuantityChanged listener) {
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
        CartItem item = cartItems.get(position);
        holder.name.setText(item.getProduct().getName());
        Glide.with(holder.itemView.getContext()).load(item.getProduct().getImageUrl()).into(holder.img);
        holder.price.setText(Utils.formatCurrency(item.getProduct().getPrice() * item.getQuantity()));

        if (holder.textWatcher != null) {
            holder.quantity.removeTextChangedListener(holder.textWatcher);
        }

        holder.quantity.setText(String.valueOf(item.getQuantity()));

        holder.textWatcher = new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int qty = Integer.parseInt(s.toString());
                    if (qty < 1) qty = 1;
                    item.setQuantity(qty);
                    holder.price.setText(Utils.formatCurrency(item.getProduct().getPrice() * qty));
                    listener.onChanged();
                } catch (NumberFormatException ignored) {}
            }
        };
        holder.quantity.addTextChangedListener(holder.textWatcher);

        holder.btnMinus.setOnClickListener(v -> {
            int qty = item.getQuantity();
            if (qty > 1) {
                item.setQuantity(qty - 1);
                notifyItemChanged(holder.getAdapterPosition());
                listener.onChanged();
            } else {
                cartItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartItems.size());
                listener.onChanged();
            }
        });

        holder.btnPlus.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(holder.getAdapterPosition());
            listener.onChanged();
        });
    }


    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        EditText quantity;
        Button btnPlus, btnMinus;
        ImageView img;
        SimpleTextWatcher textWatcher;

        ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.tvName);
            img = v.findViewById(R.id.imgFood);
            price = v.findViewById(R.id.tvPrice);
            quantity = v.findViewById(R.id.tvQuantity);
            btnPlus = v.findViewById(R.id.btnPlus);
            btnMinus = v.findViewById(R.id.btnMinus);
        }
    }
}

abstract class SimpleTextWatcher implements android.text.TextWatcher {
    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override public void afterTextChanged(Editable s) {}
    @Override public abstract void onTextChanged(CharSequence s, int start, int before, int count);
}
