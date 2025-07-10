package com.nstut.fast_food_shop.presentation.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.data.local.db.AppDatabase;
import com.nstut.fast_food_shop.data.models.ProductRoom;
import com.nstut.fast_food_shop.presentation.ui.activities.EditProductActivity;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<ProductRoom> products;
    private Context context;
    private OnProductClickListener onProductClickListener;

    public ProductAdapter(List<ProductRoom> products, OnProductClickListener onProductClickListener) {
        this.products = products;
        this.onProductClickListener = onProductClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtPrice;
        ImageView imageView;
        Button btnEdit, btnDelete;

        public ViewHolder(View view) {
            super(view);
            txtName = view.findViewById(R.id.txtName);
            txtPrice = view.findViewById(R.id.txtPrice);
            imageView = view.findViewById(R.id.imageView);
            btnEdit = view.findViewById(R.id.btnEdit);
            btnDelete = view.findViewById(R.id.btnDelete);
        }
    }

    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductAdapter.ViewHolder holder, int position) {
        ProductRoom p = products.get(position);
        holder.txtName.setText(p.getName());
        holder.txtPrice.setText(String.valueOf(p.getPrice()));
        Glide.with(context).load(p.getImageUrl()).into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            if (onProductClickListener != null) {
                onProductClickListener.onProductClick(p);
            }
        });

        // Làm mờ item nếu không available
        boolean available = p.isAvailable();
        float alpha = available ? 1.0f : 0.4f;
        holder.itemView.setAlpha(alpha);
        holder.txtName.setAlpha(alpha);
        holder.txtPrice.setAlpha(alpha);
        holder.imageView.setAlpha(alpha);
        holder.btnEdit.setAlpha(alpha);
        holder.btnDelete.setEnabled(available);

        holder.btnDelete.setOnClickListener(v -> {
            if (!available) return;
            // Cập nhật trạng thái ngay trên UI trước khi gọi DB
            p.setAvailable(false);
            p.setUpdatedAt(java.time.LocalDateTime.now().toString());
            notifyItemChanged(position);
            new Thread(() -> {
                // Soft delete: set isAvailable = false, update updatedAt
                AppDatabase.getInstance(context).productDao().update(p);
            }).start();
        });

        holder.btnEdit.setOnClickListener(v -> {
            Intent i = new Intent(context, EditProductActivity.class);
            i.putExtra("product_id", p.getProductId());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public interface OnProductClickListener {
        void onProductClick(ProductRoom product);
    }
}
