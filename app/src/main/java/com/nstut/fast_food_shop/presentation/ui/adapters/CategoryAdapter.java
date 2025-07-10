package com.nstut.fast_food_shop.presentation.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.data.models.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categories;
    private OnCategoryClickListener onCategoryClickListener;

    public CategoryAdapter(List<Category> categories, OnCategoryClickListener onCategoryClickListener) {
        this.categories = categories;
        this.onCategoryClickListener = onCategoryClickListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.categoryName.setText(category.getName());
        holder.itemView.setOnClickListener(v -> onCategoryClickListener.onCategoryClick(category));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category_name);
        }
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }
}