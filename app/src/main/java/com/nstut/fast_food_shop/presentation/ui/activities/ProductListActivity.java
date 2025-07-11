package com.nstut.fast_food_shop.presentation.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.data.local.db.AppDatabase;
import com.nstut.fast_food_shop.data.models.ProductRoom;
import com.nstut.fast_food_shop.data.models.ProductWithCategories;
import com.nstut.fast_food_shop.presentation.ui.adapters.ProductAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductListActivity extends BaseActivity implements ProductAdapter.OnProductClickListener {
    RecyclerView recyclerView;
    ProductAdapter adapter;
    List<ProductWithCategories> products;
    private ExecutorService executorService;
    private AppDatabase appDatabase;
    private ImageView backButton;

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        int categoryId = getIntent().getIntExtra("category_id", -1);
        if (categoryId != -1) {
            appDatabase.productDao().getProductsWithCategoriesByCategoryId(categoryId).observe(this, newProducts -> {
                adapter.updateProducts(newProducts);
            });
        } else {
            loadAllAvailableProducts();
        }
    }

    private void loadAllAvailableProducts() {
        appDatabase.productDao().getAvailableProductsWithCategories().observe(this, newProducts -> {
            adapter.updateProducts(newProducts);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        executorService = Executors.newSingleThreadExecutor();
        appDatabase = AppDatabase.getInstance(this);

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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        products = new ArrayList<>();

        adapter = new ProductAdapter(products, this);
        recyclerView.setAdapter(adapter);

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    @Override
    public void onProductClick(ProductRoom product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.getId());
        startActivity(intent);
    }
}
