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
import com.nstut.fast_food_shop.data.local.db.AppDatabase;
import com.nstut.fast_food_shop.data.models.ProductRoom;
import com.nstut.fast_food_shop.data.models.ProductWithCategories;
import com.nstut.fast_food_shop.presentation.ui.adapters.ProductAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CategoryProductListActivity extends BaseActivity implements ProductAdapter.OnProductClickListener {
    RecyclerView recyclerView;
    ProductAdapter adapter;
    List<ProductWithCategories> products;
    private List<ProductWithCategories> filteredProducts;
    private ExecutorService executorService;
    private AppDatabase appDatabase;
    private ImageView backButton;
    private SearchView searchView;

    private void loadData() {
        int categoryId = getIntent().getIntExtra("category_id", -1);
        if (categoryId != -1) {
            appDatabase.productDao().getProductsWithCategoriesByCategoryId(categoryId).observe(this, newProducts -> {
                products.clear();
                products.addAll(newProducts);
                filterProducts("");
            });
        } else {
            loadAllAvailableProducts();
        }
    }

    private void loadAllAvailableProducts() {
        appDatabase.productDao().getAvailableProductsWithCategories().observe(this, newProducts -> {
            products.clear();
            products.addAll(newProducts);
            filterProducts("");
        });
    }

    private void filterProducts(String query) {
        filteredProducts.clear();
        if (query.isEmpty()) {
            filteredProducts.addAll(products);
        } else {
            for (ProductWithCategories productWithCategories : products) {
                if (productWithCategories.product.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredProducts.add(productWithCategories);
                }
            }
        }
        adapter.updateProducts(filteredProducts);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_product_list);

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
        filteredProducts = new ArrayList<>();

        adapter = new ProductAdapter(filteredProducts, this);
        recyclerView.setAdapter(adapter);

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProducts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText);
                return true;
            }
        });

        loadData();
    }

    @Override
    public void onProductClick(ProductRoom product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.getId());
        startActivity(intent);
    }
}
