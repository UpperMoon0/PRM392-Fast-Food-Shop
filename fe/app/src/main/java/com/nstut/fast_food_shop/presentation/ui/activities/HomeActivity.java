package com.nstut.fast_food_shop.presentation.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nstut.fast_food_shop.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.nstut.fast_food_shop.presentation.ui.adapters.CategoryAdapter;
import com.nstut.fast_food_shop.model.Category;
import com.nstut.fast_food_shop.model.Product;
import com.nstut.fast_food_shop.presentation.ui.adapters.ProductAdapter;
import com.nstut.fast_food_shop.repository.CartRepository;
import com.nstut.fast_food_shop.repository.CategoryRepository;
import com.nstut.fast_food_shop.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity implements CategoryAdapter.OnCategoryClickListener, ProductAdapter.OnProductClickListener, ProductAdapter.OnAddToCartClickListener {

    private RecyclerView productsRecyclerView;
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private List<Category> categoryList;
    private List<Product> productList;
    private ImageView bannerImage;
    private SearchView searchView;
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private ChipGroup categoryChipGroup;
    private CartRepository cartRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        String role = sharedPreferences.getString("role", "USER");

        if (isLoggedIn && "ADMIN".equals(role)) {
            startActivity(new Intent(this, AdminProductListActivity.class));
            finish();
            return;
        }


        bannerImage = findViewById(R.id.banner_image);
        productsRecyclerView = findViewById(R.id.products_recycler_view);
        categoryRecyclerView = findViewById(R.id.category_recycler_view);
        searchView = findViewById(R.id.search_view);
        categoryChipGroup = findViewById(R.id.category_chip_group);

        productRepository = new ProductRepository();
        categoryRepository = new CategoryRepository();
        categoryList = new ArrayList<>();
        productList = new ArrayList<>();

        categoryAdapter = new CategoryAdapter(categoryList, this);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryRecyclerView.setAdapter(categoryAdapter);

        productAdapter = new ProductAdapter(new ArrayList<Product>(), this, null, this);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productsRecyclerView.setAdapter(productAdapter);

        loadData();
        setupSearch();
        setupCategoryFilter();
    }

    private void loadData() {
        categoryRepository.getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body());
                    categoryAdapter.notifyDataSetChanged();
                    updateCategoryChips(categoryList);
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e("HomeActivity", "Error loading categories", t);
            }
        });

        loadProducts();
    }

    @Override
    public void onCategoryClick(Category category) {
        Intent intent = new Intent(this, CategoryProductListActivity.class);
        intent.putExtra("category_id", String.valueOf(category.getId()));
        startActivity(intent);
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, String.valueOf(product.getId()));
        startActivity(intent);
    }

    @Override
    public void onAddToCartClick(Product product) {
        cartRepository.addItemToCart(getCurrentUser().getId(), String.valueOf(product.getId()), 1);
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchAndFilterProducts();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchAndFilterProducts();
                return true;
            }
        });
    }

    private void setupCategoryFilter() {
        categoryChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            searchAndFilterProducts();
        });
    }

    private void updateCategoryChips(List<com.nstut.fast_food_shop.model.Category> categories) {
        categoryChipGroup.removeAllViews();

        Chip allChip = new Chip(this);
        allChip.setText("All");
        allChip.setTag(-1);
        allChip.setCheckable(true);
        allChip.setChecked(true);
        categoryChipGroup.addView(allChip);

        for (com.nstut.fast_food_shop.model.Category category : categories) {
            Chip chip = new Chip(this);
            chip.setText(category.getName());
            chip.setTag(category.getId());
            chip.setCheckable(true);
            categoryChipGroup.addView(chip);
        }
    }

    private void loadProducts() {
        productRepository.getAllProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    productAdapter.updateProducts(productList);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("HomeActivity", "Error loading products", t);
            }
        });
    }

    private void searchAndFilterProducts() {
        String query = searchView.getQuery().toString().toLowerCase();
        Long selectedCategoryId = null;
        int checkedChipId = categoryChipGroup.getCheckedChipId();
        if (checkedChipId != View.NO_ID) {
            Chip selectedChip = categoryChipGroup.findViewById(checkedChipId);
            if (selectedChip != null) {
                Object tag = selectedChip.getTag();
                if (tag instanceof Long) {
                    selectedCategoryId = (Long) tag;
                }
            }
        }

        productRepository.searchProducts(query, selectedCategoryId != null ? String.valueOf(selectedCategoryId) : null).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productAdapter.updateProducts(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Log.e("HomeActivity", "Error searching products", t);
            }
        });
    }
}