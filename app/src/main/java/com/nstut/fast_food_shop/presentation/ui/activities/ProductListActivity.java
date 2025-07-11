package com.nstut.fast_food_shop.presentation.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.data.local.db.AppDatabase;
import com.nstut.fast_food_shop.data.models.ProductRoom;
import com.nstut.fast_food_shop.data.models.User;
import com.nstut.fast_food_shop.presentation.ui.adapters.ProductAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductListActivity extends BaseActivity implements ProductAdapter.OnProductClickListener, ProductAdapter.OnAdminProductClickListener {
    RecyclerView recyclerView;
    ProductAdapter adapter;
    List<ProductRoom> products;
    private ExecutorService executorService;
    private AppDatabase appDatabase;
    private User currentUser;

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        String categoryIdStr = getIntent().getStringExtra("category_id");
        executorService.execute(() -> {
            List<ProductRoom> newProducts;
            if (currentUser != null && currentUser.getRole().equals("admin")) {
                newProducts = appDatabase.productDao().getAll();
            } else {
                if (categoryIdStr != null) {
                    try {
                        int categoryId = Integer.parseInt(categoryIdStr);
                        newProducts = appDatabase.productDao().getProductsByCategory(categoryId);
                    } catch (NumberFormatException e) {
                        newProducts = appDatabase.productDao().getAllAvailable();
                    }
                } else {
                    newProducts = appDatabase.productDao().getAllAvailable();
                }
            }

            List<ProductRoom> finalNewProducts = newProducts;
            runOnUiThread(() -> {
                products.clear();
                products.addAll(finalNewProducts);
                adapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        executorService = Executors.newSingleThreadExecutor();
        appDatabase = AppDatabase.getInstance(this);
        currentUser = getCurrentUser();

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

        if (currentUser != null && currentUser.getRole().equals("admin")) {
            findViewById(R.id.secondary_header).setVisibility(View.VISIBLE);
            findViewById(R.id.button_manage_categories).setOnClickListener(v -> {
                Intent intent = new Intent(this, CategoryListActivity.class);
                startActivity(intent);
            });
            adapter = new ProductAdapter(products, (ProductAdapter.OnAdminProductClickListener) this);
        } else {
            adapter = new ProductAdapter(products, (ProductAdapter.OnProductClickListener) this);
        }
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onProductClick(ProductRoom product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT, product);
        startActivity(intent);
    }

    @Override
    public void onEditClick(ProductRoom product) {
        Intent intent = new Intent(this, AddEditProductActivity.class);
        intent.putExtra("product_id", product.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(ProductRoom product) {
        executorService.execute(() -> {
            appDatabase.productDao().delete(product);
            loadData();
        });
    }
}
