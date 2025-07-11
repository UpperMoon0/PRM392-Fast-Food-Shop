package com.nstut.fast_food_shop.presentation.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nstut.fast_food_shop.R;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        setupHeader();
    }

    public void setupHeader() {
        TextView appName = findViewById(R.id.app_name);
        Button loginLogoutButton = findViewById(R.id.login_logout_button);
        LinearLayout adminNavLinks = findViewById(R.id.admin_nav_links);
        Button manageProductsButton = findViewById(R.id.button_manage_products);
        Button manageCategoriesButton = findViewById(R.id.button_manage_categories);

        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        String role = sharedPreferences.getString("role", "user");

        if (appName != null) {
            appName.setOnClickListener(v -> {
                Intent intent;
                if (isLoggedIn && "admin".equals(role)) {
                    intent = new Intent(this, ProductListActivity.class);
                } else {
                    intent = new Intent(this, HomeActivity.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            });
        }

        if (loginLogoutButton != null) {
            if (isLoggedIn) {
                loginLogoutButton.setText("Logout");
                loginLogoutButton.setActivated(true);
                loginLogoutButton.setOnClickListener(v -> {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });

                if ("admin".equals(role) && adminNavLinks != null && manageProductsButton != null && manageCategoriesButton != null) {
                    adminNavLinks.setVisibility(View.VISIBLE);
                    manageProductsButton.setOnClickListener(v -> {
                        Intent intent = new Intent(this, ProductListActivity.class);
                        startActivity(intent);
                    });
                    manageCategoriesButton.setOnClickListener(v -> {
                        Intent intent = new Intent(this, CategoryListActivity.class);
                        startActivity(intent);
                    });
                }

            } else {
                loginLogoutButton.setText("Login");
                loginLogoutButton.setActivated(false);
                loginLogoutButton.setOnClickListener(v -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                });
            }
        }
    }
}