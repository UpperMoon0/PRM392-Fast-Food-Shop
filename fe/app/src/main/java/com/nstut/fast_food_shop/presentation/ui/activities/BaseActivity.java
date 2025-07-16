package com.nstut.fast_food_shop.presentation.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.nstut.fast_food_shop.CartActivity;
import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.data.models.User;
import com.nstut.fast_food_shop.model.CartItem;
import com.nstut.fast_food_shop.repository.CartRepository;

import java.util.List;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    private CartRepository cartRepository;

    @Override
    protected void onResume() {
        super.onResume();
        cartRepository = new CartRepository(this);
        setupHeader();
        updateCartBadge();
    }

    public User getCurrentUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id", null);
        String userName = sharedPreferences.getString("user_name", null);
        String userEmail = sharedPreferences.getString("user_email", null);
        String userRole = sharedPreferences.getString("user_role", null);

        if (userId != null) {
            User user = new User();
            user.setId(userId);
            user.setName(userName);
            user.setEmail(userEmail);
            user.setRole(userRole);
            return user;
        }
        return null;
    }

    public void setupHeader() {
        setupHeader(null, false);
    }

    public void setupHeader(boolean showBackButton) {
        setupHeader(null, showBackButton);
    }

    public void setupHeader(View secondaryHeader, boolean showBackButton) {
        Log.d(TAG, "setupHeader called in " + this.getClass().getSimpleName());
        TextView appName = findViewById(R.id.app_name);
        Button loginLogoutButton = findViewById(R.id.login_logout_button);
        ImageButton userMenuButton = findViewById(R.id.user_menu_button);
        ImageButton backButton = findViewById(R.id.back_button);

        if (backButton != null) {
            if (showBackButton) {
                backButton.setVisibility(View.VISIBLE);
                backButton.setOnClickListener(v -> {
                    finish();
                });
            } else {
                backButton.setVisibility(View.GONE);
            }
        }

        SharedPreferences sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getString("user_id", null) != null;
        String role = sharedPreferences.getString("user_role", "user");
        Log.d(TAG, "--- setupHeader in " + this.getClass().getSimpleName() + " ---");
        Log.d(TAG, "isLoggedIn from SharedPreferences: " + isLoggedIn);
        Log.d(TAG, "Role from SharedPreferences: " + role);

        if (isLoggedIn && !"admin".equalsIgnoreCase(role)) {
            loginLogoutButton.setVisibility(View.GONE);
            userMenuButton.setVisibility(View.VISIBLE);
            userMenuButton.setOnClickListener(this::showUserMenu);
        } else {
            userMenuButton.setVisibility(View.GONE);
            if (isLoggedIn) {
                loginLogoutButton.setText("Logout");
                loginLogoutButton.setOnClickListener(v -> {
                    logout();
                });
            } else {
                loginLogoutButton.setText("Login");
                loginLogoutButton.setOnClickListener(v -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                });
            }
        }

        if (appName == null) {
            Log.e(TAG, "appName TextView not found. Header not fully initialized.");
        } else {
            appName.setOnClickListener(v -> {
                Intent intent;
                SharedPreferences currentPrefs = getSharedPreferences("login_prefs", MODE_PRIVATE);
                boolean currentIsLoggedIn = currentPrefs.getString("user_id", null) != null;
                String currentRole = currentPrefs.getString("user_role", "user");
                Log.d(TAG, "App name clicked. Role: " + currentRole);
                if (currentIsLoggedIn && "admin".equalsIgnoreCase(currentRole)) {
                    intent = new Intent(this, FinanceActivity.class);
                } else {
                    intent = new Intent(this, HomeActivity.class);
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            });
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

                Button financeButton = adminNavLinks.findViewById(R.id.button_finance);
                if (financeButton != null) {
                    financeButton.setOnClickListener(v -> {
                        Log.d(TAG, "Finance button clicked.");
                        Intent intent = new Intent(this, FinanceActivity.class);
                        startActivity(intent);
                    });
                }
            } else {
                Log.e(TAG, "secondaryHeader is not a LinearLayout, cannot configure admin links.");
            }
        }
    }

    public void updateCartBadge() {
        // This method is now obsolete as the cart badge is no longer directly in the header.
        // The cart count can be managed within the menu if needed in the future.
    }

    private void showUserMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.user_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_cart) {
                startActivity(new Intent(this, CartActivity.class));
                return true;
            } else if (itemId == R.id.menu_chat) {
                startActivity(new Intent(this, ChatActivity.class));
                return true;
            } else if (itemId == R.id.menu_transaction_history) {
                startActivity(new Intent(this, TransactionHistoryActivity.class));
                return true;
            } else if (itemId == R.id.menu_logout) {
                logout();
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        SharedPreferences sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}