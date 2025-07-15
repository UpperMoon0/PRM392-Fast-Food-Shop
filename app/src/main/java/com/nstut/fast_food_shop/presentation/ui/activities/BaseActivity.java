package com.nstut.fast_food_shop.presentation.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.data.models.User;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    @Override
    protected void onResume() {
        super.onResume();
        setupHeader();
    }

    public User getCurrentUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userJson = sharedPreferences.getString("user", null);
        Log.d(TAG, "getCurrentUser: userJson from SharedPreferences: " + userJson);
        if (userJson != null) {
            User user = new Gson().fromJson(userJson, User.class);
            Log.d(TAG, "getCurrentUser: User object created from JSON. Role: " + (user != null ? user.getRole() : "null user object"));
            return user;
        }
        Log.d(TAG, "getCurrentUser: userJson is null, returning null.");
        return null;
    }

    public void setupHeader() {
        setupHeader(null);
    }
    public void setupHeader(View secondaryHeader) {
        Log.d(TAG, "setupHeader called in " + this.getClass().getSimpleName());
        TextView appName = findViewById(R.id.app_name);
        Button loginLogoutButton = findViewById(R.id.login_logout_button);
        ImageButton chatButton = findViewById(R.id.chat_button);
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        String role = sharedPreferences.getString("role", "user");
        Log.d(TAG, "--- setupHeader in " + this.getClass().getSimpleName() + " ---");
        Log.d(TAG, "isLoggedIn from SharedPreferences: " + isLoggedIn);
        Log.d(TAG, "Role from SharedPreferences: " + role);

        if (chatButton != null) {
            if (isLoggedIn) {
                chatButton.setVisibility(View.VISIBLE);
            } else {
                chatButton.setVisibility(View.GONE);
            }
        }
            chatButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, ChatActivity.class);
                startActivity(intent);
            });

        if (appName == null) {
            Log.e(TAG, "appName TextView not found. Header not fully initialized.");
        } else {
            appName.setOnClickListener(v -> {
                Intent intent;
                SharedPreferences currentPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                boolean currentIsLoggedIn = currentPrefs.getBoolean("is_logged_in", false);
                String currentRole = currentPrefs.getString("role", "user");
                Log.d(TAG, "App name clicked. Role: " + currentRole);
                if (currentIsLoggedIn && "admin".equalsIgnoreCase(currentRole)) {
                    intent = new Intent(this, AdminProductListActivity.class);
                } else {
                    intent = new Intent(this, HomeActivity.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            });
        }

        if (loginLogoutButton == null) {
            Log.e(TAG, "loginLogoutButton not found. Header not fully initialized.");
        } else {
            if (isLoggedIn) {
                loginLogoutButton.setText("Logout");
                loginLogoutButton.setActivated(true);
                loginLogoutButton.setOnClickListener(v -> {
                    Log.d(TAG, "Logout button clicked. Clearing user_prefs.");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                });
            } else {
                loginLogoutButton.setText("Login");
                loginLogoutButton.setActivated(false);
                loginLogoutButton.setOnClickListener(v -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                });
            }
        }

        if (secondaryHeader == null) {
            Log.d(TAG, "secondaryHeader is null. Admin links will not be configured.");
        } else {
            if (secondaryHeader instanceof LinearLayout) {
                LinearLayout adminNavLinks = (LinearLayout) secondaryHeader;
                Log.d(TAG, "Checking role for admin nav links visibility. Role: " + role);
                if ("admin".equalsIgnoreCase(role)) {
                    adminNavLinks.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Admin links visible.");
                } else {
                    adminNavLinks.setVisibility(View.GONE);
                    Log.d(TAG, "Admin links gone.");
                }

                Button manageProductsButton = adminNavLinks.findViewById(R.id.button_manage_products);
                if (manageProductsButton != null) {
                    manageProductsButton.setOnClickListener(v -> {
                        Log.d(TAG, "Manage Products button clicked.");
                        Intent intent = new Intent(this, AdminProductListActivity.class);
                        startActivity(intent);
                    });
                }

                Button manageCategoriesButton = adminNavLinks.findViewById(R.id.button_manage_categories);
                if (manageCategoriesButton != null) {
                    manageCategoriesButton.setOnClickListener(v -> {
                        Log.d(TAG, "Manage Categories button clicked.");
                        Intent intent = new Intent(this, CategoryListActivity.class);
                        startActivity(intent);
                    });
                }
            } else {
                Log.e(TAG, "secondaryHeader is not a LinearLayout, cannot configure admin links.");
            }
        }
    }
}