package com.nstut.fast_food_shop.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nstut.fast_food_shop.databinding.ItemFoodBinding;
import com.nstut.fast_food_shop.model.FoodItem;
import com.nstut.fast_food_shop.util.Utils;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    public interface OnCartChangeListener {
        void onAdd(FoodItem item);
    }

    private final List<FoodItem> data;
    private final OnCartChangeListener listener;

    public OrderAdapter(List<FoodItem> data, OnCartChangeListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFoodBinding binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        FoodItem item = data.get(pos);

        h.binding.tvFoodName.setText(item.getName());
        h.binding.tvFoodPrice.setText(Utils.formatCurrency(item.getPrice()));
        h.binding.imgFood.setImageResource(item.getImageResId());

        if (h.currentWatcher != null) {
            h.binding.tvQuantity.removeTextChangedListener(h.currentWatcher);
        }

        h.binding.tvQuantity.setText(String.valueOf(item.getQuantity()));

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                try {
                    int qty = Integer.parseInt(s.toString());
                    if (qty > 0) {
                        item.setQuantity(qty);
                        listener.onAdd(item);
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        };
        h.binding.tvQuantity.addTextChangedListener(watcher);
        h.currentWatcher = watcher;

        // Các nút + -
        h.binding.btnAdd.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(h.getAdapterPosition());
            listener.onAdd(item);
        });

        h.binding.btnMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(h.getAdapterPosition());
                listener.onAdd(item);
            }
        });
    }


    @Override
    public int getItemCount() { return data.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemFoodBinding binding;
        TextWatcher currentWatcher;

        ViewHolder(ItemFoodBinding b) {
            super(b.getRoot());
            this.binding = b;
        }
    }
}
