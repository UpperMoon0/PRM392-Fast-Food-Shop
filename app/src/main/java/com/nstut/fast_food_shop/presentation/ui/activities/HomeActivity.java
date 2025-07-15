package com.nstut.fast_food_shop.presentation.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.data.local.db.AppDatabase;
import com.nstut.fast_food_shop.data.models.Category;
import com.nstut.fast_food_shop.data.models.ProductRoom;
import com.nstut.fast_food_shop.data.models.User;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.nstut.fast_food_shop.presentation.ui.adapters.CategoryAdapter;
import com.nstut.fast_food_shop.presentation.ui.adapters.ProductAdapter;
import com.nstut.fast_food_shop.data.models.ProductWithCategories;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeActivity extends BaseActivity implements CategoryAdapter.OnCategoryClickListener, ProductAdapter.OnProductClickListener {

    private RecyclerView productsRecyclerView;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private List<Category> categoryList;
    private List<ProductWithCategories> productList;
    private List<ProductWithCategories> filteredProductList;
    private AppDatabase appDatabase;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private ImageView bannerImage;
    private Button loginLogoutButton;
    private ImageButton chatButton;
    private SearchView searchView;
    private ChipGroup categoryChipGroup;

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
        chatButton = findViewById(R.id.chat_button);
        productsRecyclerView = findViewById(R.id.products_recycler_view);
        searchView = findViewById(R.id.search_view);
        categoryChipGroup = findViewById(R.id.category_chip_group);

        categoryList = new ArrayList<>();
        productList = new ArrayList<>();
        filteredProductList = new ArrayList<>();

        categoryAdapter = new CategoryAdapter(categoryList, this);

        productAdapter = new ProductAdapter(new ArrayList<>(), this);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productsRecyclerView.setAdapter(productAdapter);

        loadData();
        setupSearch();
        setupCategoryFilter();
        setupChatButton();
    }

    private void loadData() {
        appDatabase.categoryDao().getAllCategories().observe(this, categories -> {
            categoryList.clear();
            categoryList.addAll(categories);
            categoryAdapter.notifyDataSetChanged();
            updateCategoryChips(categories);
        });

        appDatabase.productDao().getProductsWithCategories().observe(this, productsWithCategories -> {
            productList.clear();
            productList.addAll(productsWithCategories);
            filterProducts();
        });
    }

    @Override
    public void onCategoryClick(Category category) {
        for (int i = 0; i < categoryChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) categoryChipGroup.getChildAt(i);
            if (chip.getTag().equals(category.getId())) {
                chip.setChecked(true);
                break;
            }
        }
    }

    @Override
    public void onProductClick(ProductRoom product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.getId());
        startActivity(intent);
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProducts();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts();
                return true;
            }
        });
    }

    private void setupCategoryFilter() {
        categoryChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            filterProducts();
        });
    }

    private void updateCategoryChips(List<Category> categories) {
        categoryChipGroup.removeAllViews();

        Chip allChip = new Chip(this);
        allChip.setText("All");
        allChip.setTag(-1);
        allChip.setCheckable(true);
        allChip.setChecked(true);
        categoryChipGroup.addView(allChip);

        for (Category category : categories) {
            Chip chip = new Chip(this);
            chip.setText(category.getName());
            chip.setTag(category.getId());
            chip.setCheckable(true);
            categoryChipGroup.addView(chip);
        }
    }

    private void setupChatButton() {
        chatButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
            startActivity(intent);
        });
    }

    private void filterProducts() {
        String query = searchView.getQuery().toString().toLowerCase();
        int selectedCategoryId = -1;
        int checkedChipId = categoryChipGroup.getCheckedChipId();
        if (checkedChipId != View.NO_ID) {
            Chip selectedChip = categoryChipGroup.findViewById(checkedChipId);
            if (selectedChip != null) {
                Object tag = selectedChip.getTag();
                if (tag != null) {
                    selectedCategoryId = (int) tag;
                }
            }
        }
        final int finalSelectedCategoryId = selectedCategoryId;

        filteredProductList.clear();
        for (ProductWithCategories productWithCategories : productList) {
            ProductRoom product = productWithCategories.product;
            boolean matchesCategory = finalSelectedCategoryId == -1 ||
                    productWithCategories.categories.stream().anyMatch(c -> c.getId() == finalSelectedCategoryId);
            boolean matchesSearch = query.isEmpty() || product.getName().toLowerCase().contains(query);

            if (matchesCategory && matchesSearch) {
                filteredProductList.add(productWithCategories);
            }
        }
        productAdapter.updateProducts(filteredProductList);
    }
}