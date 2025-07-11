package com.nstut.fast_food_shop.presentation.ui.adapters;

import android.content.Context;
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
import com.nstut.fast_food_shop.data.models.ProductRoom;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<ProductRoom> products;
    private Context context;
    private OnProductClickListener productClickListener;
    private OnAdminProductClickListener adminProductClickListener;

    public ProductAdapter(List<ProductRoom> products, OnProductClickListener listener) {
        this.products = products;
        this.productClickListener = listener;
    }

    public ProductAdapter(List<ProductRoom> products, OnAdminProductClickListener listener) {
        this.products = products;
        this.adminProductClickListener = listener;
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

        if (adminProductClickListener != null) {
            holder.adminActions.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> adminProductClickListener.onEditClick(p));
            holder.btnDelete.setOnClickListener(v -> adminProductClickListener.onDeleteClick(p));
        } else if (productClickListener != null) {
            holder.adminActions.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(v -> productClickListener.onProductClick(p));
        } else {
            holder.adminActions.setVisibility(View.GONE);
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
