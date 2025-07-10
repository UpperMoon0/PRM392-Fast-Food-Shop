package com.nstut.fast_food_shop.presentation.ui.activities;

import android.os.Bundle;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.data.models.Category;
import com.nstut.fast_food_shop.data.local.db.AppDatabase;
import com.nstut.fast_food_shop.data.models.Category;
import com.nstut.fast_food_shop.data.models.ProductRoom;
import com.nstut.fast_food_shop.presentation.ui.adapters.CategoryAdapter;
import com.nstut.fast_food_shop.presentation.ui.adapters.ProductAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeActivity extends BaseActivity implements CategoryAdapter.OnCategoryClickListener, ProductAdapter.OnProductClickListener {

    private RecyclerView categoriesRecyclerView;
    private RecyclerView productsRecyclerView;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private List<Category> categoryList;
    private List<ProductRoom> productList;
    private AppDatabase appDatabase;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        appDatabase = AppDatabase.getInstance(this);

        categoriesRecyclerView = findViewById(R.id.categories_recycler_view);
        productsRecyclerView = findViewById(R.id.products_recycler_view);

        categoryList = new ArrayList<>();
        productList = new ArrayList<>();

        categoryAdapter = new CategoryAdapter(categoryList, this);
        categoriesRecyclerView.setAdapter(categoryAdapter);

        productAdapter = new ProductAdapter(productList, this);
        productsRecyclerView.setAdapter(productAdapter);

        loadData();
    }

    private void loadData() {
        executorService.execute(() -> {
            // Add sample categories if none exist
            if (appDatabase.categoryDao().getAllCategories().isEmpty()) {
                appDatabase.categoryDao().insertAll(
                        new Category("1", "Burgers"),
                        new Category("2", "Pizzas"),
                        new Category("3", "Drinks")
                );
            }
            List<Category> categories = appDatabase.categoryDao().getAllCategories();
            List<ProductRoom> products = appDatabase.productDao().getAllAvailable();

            runOnUiThread(() -> {
                categoryList.clear();
                categoryList.addAll(categories);
                categoryAdapter.notifyDataSetChanged();

                productList.clear();
                productList.addAll(products);
                productAdapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    public void onCategoryClick(Category category) {
        Intent intent = new Intent(this, ProductListActivity.class);
        intent.putExtra("category_id", category.getId());
        startActivity(intent);
    }

    @Override
    public void onProductClick(ProductRoom product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT, product);
        startActivity(intent);
    }
}