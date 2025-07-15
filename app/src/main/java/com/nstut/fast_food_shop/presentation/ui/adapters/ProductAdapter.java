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
import com.nstut.fast_food_shop.data.models.Category;
import com.nstut.fast_food_shop.data.models.ProductRoom;
import com.nstut.fast_food_shop.data.models.ProductWithCategories;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<ProductWithCategories> products;
    private List<ProductRoom> productRooms;
    private Context context;
    private OnProductClickListener productClickListener;
    private OnAdminProductClickListener adminProductClickListener;

    public ProductAdapter(List<ProductWithCategories> products, Object listener) {
        this.products = products;
        if (listener instanceof OnProductClickListener) {
            this.productClickListener = (OnProductClickListener) listener;
        }
        if (listener instanceof OnAdminProductClickListener) {
            this.adminProductClickListener = (OnAdminProductClickListener) listener;
        }
    }

    public ProductAdapter(List<ProductRoom> productRooms, OnProductClickListener listener) {
        this.productRooms = productRooms;
        this.productClickListener = listener;
    }

    public void updateProducts(List<ProductWithCategories> newProducts) {
        this.products.clear();
        this.products.addAll(newProducts);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtPrice, txtCategories;
        ImageView imageView;
        View adminActions;
        Button btnEdit, btnDelete;

        public ViewHolder(View view) {
            super(view);
            if (productRooms != null) {
                txtName = view.findViewById(R.id.product_name);
                txtPrice = view.findViewById(R.id.product_price);
                imageView = view.findViewById(R.id.product_image);
            } else {
                txtName = view.findViewById(R.id.txtName);
                txtPrice = view.findViewById(R.id.txtPrice);
                txtCategories = view.findViewById(R.id.txtCategories);
                imageView = view.findViewById(R.id.imageView);
                adminActions = view.findViewById(R.id.admin_action);
                btnEdit = view.findViewById(R.id.button_edit);
                btnDelete = view.findViewById(R.id.button_delete);
            }
        }
    }

    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view;
        if (productRooms != null) {
            view = LayoutInflater.from(context).inflate(R.layout.product_recommendation_item, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductAdapter.ViewHolder holder, int position) {
        if (products != null) {
            ProductWithCategories p = products.get(position);
            ProductRoom product = p.product;
            Log.d("ProductAdapter", "Binding view for position: " + position + ", product: " + product.getName());
            holder.txtName.setText(product.getName());
            holder.txtPrice.setText(String.valueOf(product.getPrice()));
            Glide.with(context).load(product.getImageUrl()).into(holder.imageView);

            String categories = p.categories.stream().map(Category::getName).collect(Collectors.joining(", "));
            holder.txtCategories.setText(categories);

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


            // Làm mờ item nếu không available
            boolean available = product.isAvailable();
            float alpha = available ? 1.0f : 0.4f;
            holder.itemView.setAlpha(alpha);
            holder.txtName.setAlpha(alpha);
            holder.txtPrice.setAlpha(alpha);
            holder.imageView.setAlpha(alpha);
        } else {
            ProductRoom product = productRooms.get(position);
            holder.txtName.setText(product.getName());
            holder.txtPrice.setText(String.valueOf(product.getPrice()));
            Glide.with(context).load(product.getImageUrl()).into(holder.imageView);
            holder.itemView.setOnClickListener(v -> productClickListener.onProductClick(product));
        }
    }

    @Override
    public int getItemCount() {
        if (products != null) {
            return products.size();
        } else {
            return productRooms.size();
        }
    }

    public interface OnProductClickListener {
        void onProductClick(ProductRoom product);
    }

    public interface OnAdminProductClickListener {
        void onEditClick(ProductRoom product);
        void onDeleteClick(ProductRoom product);
    }
}
