package com.nstut.fast_food_shop.presentation.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.model.Product;
import com.nstut.fast_food_shop.presentation.ui.adapters.ProductAdapter;
import com.nstut.fast_food_shop.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CategoryProductListActivity extends BaseActivity implements ProductAdapter.OnProductClickListener {
    RecyclerView recyclerView;
    ProductAdapter adapter;
    private ExecutorService executorService;
    private ImageView backButton;
    private SearchView searchView;
    private ProductRepository productRepository;

    private void loadData() {
        String categoryId = getIntent().getStringExtra("category_id");
        if (categoryId != null) {
            // TODO: Call ProductRepository to get products by category
        } else {
            loadAllAvailableProducts();
        }
    }

    private void loadAllAvailableProducts() {
        // TODO: Call ProductRepository to get all available products
        searchProducts("");
    }

    private void searchProducts(String query) {
        String categoryId = getIntent().getStringExtra("category_id");
        productRepository.searchProducts(query, categoryId).enqueue(new retrofit2.Callback<List<Product>>() {
            @Override
            public void onResponse(retrofit2.Call<List<Product>> call, retrofit2.Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateProducts(response.body());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<Product>> call, Throwable t) {
                // Handle failure
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_product_list);

        productRepository = new ProductRepository();
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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ProductAdapter(new ArrayList<>(), this, null, null);
        recyclerView.setAdapter(adapter);

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProducts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchProducts(newText);
                return true;
            }
        });

        loadData();
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.getId());
        startActivity(intent);
    }
}
