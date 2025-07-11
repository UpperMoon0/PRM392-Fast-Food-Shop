package com.nstut.fast_food_shop.presentation.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.nstut.fast_food_shop.presentation.ui.activities.AddEditProductActivity;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<ProductRoom> products;
    private Context context;
    private OnProductClickListener onProductClickListener;
    private OnAdminProductClickListener onAdminProductClickListener;

    public ProductAdapter(List<ProductRoom> products, OnProductClickListener onProductClickListener) {
        this.products = products;
        this.onProductClickListener = onProductClickListener;
    }

    public ProductAdapter(List<ProductRoom> products, OnAdminProductClickListener onAdminProductClickListener) {
        this.products = products;
        this.onAdminProductClickListener = onAdminProductClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtPrice;
        ImageView imageView;
        View adminActions;
        Button btnEdit, btnDelete;

        public ViewHolder(View view) {
            super(view);
            txtName = view.findViewById(R.id.txtName);
            txtPrice = view.findViewById(R.id.txtPrice);
            imageView = view.findViewById(R.id.imageView);
            adminActions = view.findViewById(R.id.admin_action);
            btnEdit = view.findViewById(R.id.button_edit);
            btnDelete = view.findViewById(R.id.button_delete);
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
        Log.d("ProductAdapter", "Binding view for position: " + position + ", product: " + p.getName());
        holder.txtName.setText(p.getName());
        holder.txtPrice.setText(String.valueOf(p.getPrice()));
        Glide.with(context).load(p.getImageUrl()).into(holder.imageView);

        if (onAdminProductClickListener != null) {
            holder.adminActions.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> onAdminProductClickListener.onEditClick(p));
            holder.btnDelete.setOnClickListener(v -> onAdminProductClickListener.onDeleteClick(p));
        } else {
            holder.adminActions.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(v -> {
                if (onProductClickListener != null) {
                    onProductClickListener.onProductClick(p);
                }
            });
        }


        // Làm mờ item nếu không available
        boolean available = p.isAvailable();
        float alpha = available ? 1.0f : 0.4f;
        holder.itemView.setAlpha(alpha);
        holder.txtName.setAlpha(alpha);
        holder.txtPrice.setAlpha(alpha);
        holder.imageView.setAlpha(alpha);
    }

    @Override
    public int getItemCount() {
        int count = products.size();
        Log.d("ProductAdapter", "getItemCount: " + count);
        return count;
    }

    public interface OnProductClickListener {
        void onProductClick(ProductRoom product);
    }

    public interface OnAdminProductClickListener {
        void onEditClick(ProductRoom product);
        void onDeleteClick(ProductRoom product);
    }
}
