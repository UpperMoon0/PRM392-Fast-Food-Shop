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
import com.nstut.fast_food_shop.data.local.db.AppDatabase;
import com.nstut.fast_food_shop.data.models.Category;
import com.nstut.fast_food_shop.data.models.User;
import com.nstut.fast_food_shop.presentation.ui.adapters.CategoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class CategoryListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList = new ArrayList<>();
    private AppDatabase appDatabase;
    private static final String TAG = "CategoryListActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        appDatabase = AppDatabase.getInstance(this);

        recyclerView = findViewById(R.id.category_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.button_manage_products).setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminProductListActivity.class);
            startActivity(intent);
        });
        categoryAdapter = new CategoryAdapter(categoryList, new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Category category) {
                Intent intent = new Intent(CategoryListActivity.this, AddEditCategoryActivity.class);
                intent.putExtra("category_id", category.getId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Category category) {
                new Thread(() -> {
                    appDatabase.categoryDao().delete(category);
                    loadCategories();
                }).start();
            }
        });


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
        appDatabase.categoryDao().getAllCategories().observe(this, categories -> {
            categoryList.clear();
            categoryList.addAll(categories);
            categoryAdapter.notifyDataSetChanged();
        });
    }
}