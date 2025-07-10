package com.nstut.fast_food_shop.presentation.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.data.models.Category;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList;
    private OnItemClickListener listener;
    private OnCategoryClickListener categoryClickListener;

    public interface OnItemClickListener {
        void onEditClick(Category category);
        void onDeleteClick(Category category);
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(List<Category> categoryList, OnItemClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    public CategoryAdapter(List<Category> categoryList, OnCategoryClickListener categoryClickListener) {
        this.categoryList = categoryList;
        this.categoryClickListener = categoryClickListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.textViewName.setText(category.getName());
        holder.textViewDescription.setText(category.getDescription());
        if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext()).load(category.getImageUrl()).into(holder.imageView);
        }

        if (listener != null) {
            holder.itemView.findViewById(R.id.button_edit).setOnClickListener(v -> listener.onEditClick(category));
            holder.itemView.findViewById(R.id.button_delete).setOnClickListener(v -> listener.onDeleteClick(category));
        } else if (categoryClickListener != null) {
            holder.itemView.setOnClickListener(v -> categoryClickListener.onCategoryClick(category));
            holder.itemView.findViewById(R.id.button_edit).setVisibility(View.GONE);
            holder.itemView.findViewById(R.id.button_delete).setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewDescription;
        ImageView imageView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_category_name);
            textViewDescription = itemView.findViewById(R.id.text_view_category_description);
            imageView = itemView.findViewById(R.id.image_view_category);
        }
    }
}