package com.nstut.fast_food_shop.presentation.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.data.models.Category;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CategorySelectionAdapter extends RecyclerView.Adapter<CategorySelectionAdapter.CategoryViewHolder> {

    private List<Category> categoryList = new ArrayList<>();
    private Set<Integer> selectedCategoryIds = new HashSet<>();

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_checkbox_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void setCategories(List<Category> categories) {
        this.categoryList = categories;
        notifyDataSetChanged();
    }

    public void setSelectedCategoryIds(List<Integer> categoryIds) {
        this.selectedCategoryIds.clear();
        if (categoryIds != null) {
            this.selectedCategoryIds.addAll(categoryIds);
        }
        notifyDataSetChanged();
    }

    public List<Integer> getSelectedCategoryIds() {
        return new ArrayList<>(selectedCategoryIds);
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox_category);
        }

        void bind(final Category category) {
            checkBox.setText(category.getName());
            checkBox.setChecked(selectedCategoryIds.contains(category.getId()));

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedCategoryIds.add(category.getId());
                } else {
                    selectedCategoryIds.remove(category.getId());
                }
            });
        }
    }
}