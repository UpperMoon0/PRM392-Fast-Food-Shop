package com.nstut.fast_food_shop.adapter;

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

        h.binding.tvQuantity.setText(String.valueOf(item.getQuantity()));

        h.binding.btnAdd.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(pos);
            listener.onAdd(item);
        });

        h.binding.btnMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(pos);
                listener.onAdd(item);
            }
        });
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemFoodBinding binding;
        ViewHolder(ItemFoodBinding b) {
            super(b.getRoot());
            this.binding = b;
        }
    }
}
