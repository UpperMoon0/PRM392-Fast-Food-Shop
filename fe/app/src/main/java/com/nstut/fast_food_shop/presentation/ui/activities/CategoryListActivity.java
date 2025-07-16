package com.nstut.fast_food_shop.presentation.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.model.Category;
import com.nstut.fast_food_shop.presentation.ui.adapters.CategoryAdapter;
import com.nstut.fast_food_shop.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.List;

public class CategoryListActivity extends BaseActivity implements CategoryAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList = new ArrayList<>();
    private CategoryRepository categoryRepository;
    private static final String TAG = "CategoryListActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);


        recyclerView = findViewById(R.id.category_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryRepository = new CategoryRepository();

        findViewById(R.id.button_manage_products).setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminProductListActivity.class);
            startActivity(intent);
        });
        categoryAdapter = new CategoryAdapter(categoryList, this);
        recyclerView.setAdapter(categoryAdapter);

        FloatingActionButton fab = findViewById(R.id.fab_add_category);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CategoryListActivity.this, AddEditCategoryActivity.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        setupHeader(findViewById(R.id.secondary_header), true);
        loadCategories();
        findViewById(R.id.button_manage_categories).setEnabled(false);
    }

    private void loadCategories() {
        categoryRepository.getAllCategories().enqueue(new retrofit2.Callback<List<Category>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Category>> call, retrofit2.Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body());
                    categoryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<Category>> call, Throwable t) {
                Log.e(TAG, "Error loading categories", t);
            }
        });
    }

    @Override
    public void onEditClick(Category category) {
        Intent intent = new Intent(CategoryListActivity.this, AddEditCategoryActivity.class);
        intent.putExtra("category_id", String.valueOf(category.getId()));
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Category category) {
        categoryRepository.deleteCategory(String.valueOf(category.getId())).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    loadCategories();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                Log.e(TAG, "Error deleting category", t);
            }
        });
    }
}