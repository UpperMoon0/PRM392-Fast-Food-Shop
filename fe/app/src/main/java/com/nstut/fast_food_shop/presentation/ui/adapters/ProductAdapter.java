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
import com.bumptech.glide.Glide;
import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.model.Category;
import com.nstut.fast_food_shop.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<Product> products;
    private Context context;
    private OnProductClickListener productClickListener;
    private OnAdminProductClickListener adminProductClickListener;
    private OnAddToCartClickListener addToCartClickListener;

    public ProductAdapter(List<Product> products, OnProductClickListener productClickListener, OnAdminProductClickListener adminProductClickListener, OnAddToCartClickListener addToCartClickListener) {
        this.products = products;
        this.productClickListener = productClickListener;
        this.adminProductClickListener = adminProductClickListener;
        this.addToCartClickListener = addToCartClickListener;
    }

    public void updateProducts(List<Product> newProducts) {
        if (this.products != newProducts) {
            this.products.clear();
            this.products.addAll(newProducts);
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtPrice, txtCategories;
        ImageView imageView;
        View adminActions;
        Button btnEdit, btnDelete, btnAddToCart;

        public ViewHolder(View view) {
            super(view);
            txtName = view.findViewById(R.id.txtName);
            txtPrice = view.findViewById(R.id.txtPrice);
            txtCategories = view.findViewById(R.id.txtCategories);
            imageView = view.findViewById(R.id.imageView);
            adminActions = view.findViewById(R.id.admin_action);
            btnEdit = view.findViewById(R.id.button_edit);
            btnDelete = view.findViewById(R.id.button_delete);
            btnAddToCart = view.findViewById(R.id.button_add_to_cart);
        }
    }

    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductAdapter.ViewHolder holder, int position) {
        Product product = products.get(position);
        Log.d("ProductAdapter", "Binding view for position: " + position + ", product: " + product.getName());
        holder.txtName.setText(product.getName());
        holder.txtPrice.setText("$" + product.getPrice());
        Glide.with(context).load(product.getImageUrl()).into(holder.imageView);

        if (product.getCategories() != null) {
            holder.txtCategories.setText(product.getCategories().stream().map(Category::getName).collect(Collectors.joining(", ")));
        }

        if (adminProductClickListener != null) {
            holder.adminActions.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> adminProductClickListener.onEditClick(product));
            holder.btnDelete.setOnClickListener(v -> adminProductClickListener.onDeleteClick(product));
        } else if (productClickListener != null) {
            holder.adminActions.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(v -> productClickListener.onProductClick(product));
        } else {
            holder.adminActions.setVisibility(View.GONE);
        }

        if (addToCartClickListener != null) {
            holder.btnAddToCart.setVisibility(View.VISIBLE);
            holder.btnAddToCart.setOnClickListener(v -> addToCartClickListener.onAddToCartClick(product));
        } else {
            holder.btnAddToCart.setVisibility(View.GONE);
        }

        // Làm mờ item nếu không available
        boolean available = product.isAvailable();
        float alpha = available ? 1.0f : 0.4f;
        holder.itemView.setAlpha(alpha);
        holder.txtName.setAlpha(alpha);
        holder.txtPrice.setAlpha(alpha);
        holder.imageView.setAlpha(alpha);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public static interface OnAdminProductClickListener {
        void onEditClick(Product product);
        void onDeleteClick(Product product);
    }

    public static interface OnAddToCartClickListener {
        void onAddToCartClick(Product product);
    }
}
