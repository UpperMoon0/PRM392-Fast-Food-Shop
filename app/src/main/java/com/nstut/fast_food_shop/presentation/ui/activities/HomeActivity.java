package com.nstut.fast_food_shop.presentation.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.data.local.db.AppDatabase;
import com.nstut.fast_food_shop.data.models.Category;
import com.nstut.fast_food_shop.data.models.ProductRoom;
import com.nstut.fast_food_shop.data.models.User;
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
    private ImageView bannerImage;
    private Button loginLogoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        String role = sharedPreferences.getString("role", User.ROLE_USER);

        if (isLoggedIn && User.ROLE_ADMIN.equals(role)) {
            startActivity(new Intent(this, AdminProductListActivity.class));
            finish();
            return;
        }

        appDatabase = AppDatabase.getInstance(this);

        bannerImage = findViewById(R.id.banner_image);
        loginLogoutButton = findViewById(R.id.login_logout_button);
        categoriesRecyclerView = findViewById(R.id.categories_recycler_view);
        productsRecyclerView = findViewById(R.id.products_recycler_view);

        categoryList = new ArrayList<>();
        productList = new ArrayList<>();

        categoryAdapter = new CategoryAdapter(categoryList, this);
        categoriesRecyclerView.setAdapter(categoryAdapter);

        productAdapter = new ProductAdapter(productList, this);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productsRecyclerView.setAdapter(productAdapter);

        loadData();
    }

    private void loadData() {
        executorService.execute(() -> {
            List<Category> categories = appDatabase.categoryDao().getAllCategories();
            List<ProductRoom> products = appDatabase.productDao().getAllAvailable();
            Log.d("HomeActivity", "Products fetched: " + products.size());

            runOnUiThread(() -> {
                categoryList.clear();
                categoryList.addAll(categories);
                categoryAdapter.notifyDataSetChanged();

                productList.clear();
                productList.addAll(products);
                Log.d("HomeActivity", "Product list size before notifying adapter: " + productList.size());
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