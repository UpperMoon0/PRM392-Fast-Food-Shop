package com.nstut.fast_food_shop.presentation.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.model.Product;
import com.nstut.fast_food_shop.presentation.ui.adapters.ProductAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminProductListActivity extends BaseActivity implements ProductAdapter.OnAdminProductClickListener {
    RecyclerView recyclerView;
    ProductAdapter adapter;
    List<Product> products;
    private ExecutorService executorService;

    @Override
    protected void onResume() {
        super.onResume();
        setupHeader(findViewById(R.id.secondary_header), false);
        loadData();
        findViewById(R.id.button_manage_products).setEnabled(false);
    }

    private void loadData() {
        // TODO: Call ProductRepository to get all products
        products.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_list);

        executorService = Executors.newSingleThreadExecutor();

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets insetsCompat = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insetsCompat.left, insetsCompat.top, insetsCompat.right, insetsCompat.bottom);
            return insets;
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton fab = findViewById(R.id.fabAddProduct);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditProductActivity.class);
            startActivity(intent);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        products = new ArrayList<>();

        findViewById(R.id.button_manage_categories).setOnClickListener(v -> {
            Intent intent = new Intent(this, CategoryListActivity.class);
            startActivity(intent);
        });
        adapter = new ProductAdapter(products, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onEditClick(Product product) {
        Intent intent = new Intent(this, AddEditProductActivity.class);
        intent.putExtra("product_id", product.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Product product) {
        // TODO: Call ProductRepository to delete the product
        loadData();
    }
}