package com.nstut.fast_food_shop.presentation.ui.activities;

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

        appDatabase = AppDatabase.getInstance(this);

        bannerImage = findViewById(R.id.banner_image);
        loginLogoutButton = findViewById(R.id.login_logout_button);
        categoriesRecyclerView = findViewById(R.id.categories_recycler_view);
        productsRecyclerView = findViewById(R.id.products_recycler_view);

        categoryList = new ArrayList<>();
        productList = new ArrayList<>();

        categoryAdapter = new CategoryAdapter(categoryList, (CategoryAdapter.OnCategoryClickListener) this);
        categoriesRecyclerView.setAdapter(categoryAdapter);

        productAdapter = new ProductAdapter(productList, this);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productsRecyclerView.setAdapter(productAdapter);

        loginLogoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        loadData();
    }

    private void loadData() {
        executorService.execute(() -> {
            // Add sample categories if none exist
            if (appDatabase.categoryDao().getAllCategories().isEmpty()) {
                appDatabase.categoryDao().insert(new Category("Burgers", "Delicious burgers", ""));
                appDatabase.categoryDao().insert(new Category("Pizzas", "Cheesy pizzas", ""));
                appDatabase.categoryDao().insert(new Category("Drinks", "Refreshing drinks", ""));
            }
            if (appDatabase.productDao().getAll().isEmpty()) {
                appDatabase.productDao().insert(new ProductRoom(0, "Hamburger", "A classic hamburger", 5.99, "https://www.foodandwine.com/thmb/DI29Houjc_ccAtFKly0BbVsusHc=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/crispy-comte-cheesburgers-FT-RECIPE0921-6166c6552b7148e8a8561f77657f590b.jpg", 1, true, "", ""));
                appDatabase.productDao().insert(new ProductRoom(0, "Cheeseburger", "A cheeseburger", 6.99, "https://www.foodandwine.com/thmb/DI29Houjc_ccAtFKly0BbVsusHc=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/crispy-comte-cheesburgers-FT-RECIPE0921-6166c6552b7148e8a8561f77657f590b.jpg", 1, true, "", ""));
                appDatabase.productDao().insert(new ProductRoom(0, "Pizza", "A pizza", 8.99, "https://www.foodandwine.com/thmb/DI29Houjc_ccAtFKly0BbVsusHc=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/crispy-comte-cheesburgers-FT-RECIPE0921-6166c6552b7148e8a8561f77657f590b.jpg", 2, true, "", ""));
                appDatabase.productDao().insert(new ProductRoom(0, "Coke", "A coke", 1.99, "https://www.foodandwine.com/thmb/DI29Houjc_ccAtFKly0BbVsusHc=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/crispy-comte-cheesburgers-FT-RECIPE0921-6166c6552b7148e8a8561f77657f590b.jpg", 3, true, "", ""));
            }
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