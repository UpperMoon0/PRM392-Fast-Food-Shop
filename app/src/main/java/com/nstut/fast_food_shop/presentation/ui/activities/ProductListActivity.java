package com.nstut.fast_food_shop.presentation.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

public class ProductListActivity extends BaseActivity implements ProductAdapter.OnProductClickListener {
    RecyclerView recyclerView;
    ProductAdapter adapter;
    List<ProductRoom> products;
    Button loginLogoutButton;
    private ExecutorService executorService;
    private AppDatabase appDatabase;
    private User currentUser;

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
        checkUserLoginStatus();
    }

    private void checkUserRole() {
        FloatingActionButton fab = findViewById(R.id.fabAddProduct);
        View secondaryHeader = findViewById(R.id.secondary_header);
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userRole = sharedPreferences.getString("user_role", null);
        if (User.ROLE_ADMIN.equals(userRole)) {
            fab.setVisibility(View.VISIBLE);
            secondaryHeader.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
            secondaryHeader.setVisibility(View.GONE);
        }
    }

    private void loadData() {
        String categoryIdStr = getIntent().getStringExtra("category_id");
        executorService.execute(() -> {
            List<ProductRoom> newProducts;
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
        adapter = new ProductAdapter(products, this);
        recyclerView.setAdapter(adapter);

        loginLogoutButton = findViewById(R.id.login_logout_button);
        loginLogoutButton.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            if (sharedPreferences.getString("user_id", null) != null || sharedPreferences.getString("user_role", null) != null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("user_id");
                editor.remove("user_role");
                editor.apply();
                checkUserLoginStatus();
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
        });

        Button btnCategoryList = findViewById(R.id.btn_category_list);
        btnCategoryList.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageCategoryActivity.class);
            startActivity(intent);
        });
    }

    private void checkUserLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        if (sharedPreferences.getString("user_id", null) != null || User.ROLE_ADMIN.equals(sharedPreferences.getString("user_role", null))) {
            loginLogoutButton.setText("Logout");
            loginLogoutButton.setBackgroundColor(ActivityCompat.getColor(this, R.color.red));
        } else {
            loginLogoutButton.setText("Login");
            loginLogoutButton.setBackgroundColor(ActivityCompat.getColor(this, R.color.blue));
        }
        checkUserRole();
    }

    @Override
    public void onProductClick(ProductRoom product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT, product);
        startActivity(intent);
    }
}
