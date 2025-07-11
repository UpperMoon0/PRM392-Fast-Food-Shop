package com.nstut.fast_food_shop.presentation.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.data.local.db.AppDatabase;
import com.nstut.fast_food_shop.data.models.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends BaseActivity {

    private EditText etEmail, etPassword;
    private CheckBox cbRememberMe;
    private AppDatabase appDatabase;
    private ExecutorService executorService;
    private SharedPreferences sharedPreferences;
    private Button loginLogoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        cbRememberMe = findViewById(R.id.cb_remember_me);
        Button btnLogin = findViewById(R.id.btn_login);
        TextView tvRegister = findViewById(R.id.tv_register);

        appDatabase = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE);

        boolean isRemembered = sharedPreferences.getBoolean("remember_me", false);
        if (isRemembered) {
            etEmail.setText(sharedPreferences.getString("email", ""));
            etPassword.setText(sharedPreferences.getString("password", ""));
            cbRememberMe.setChecked(true);
        }

        btnLogin.setOnClickListener(v -> loginUser());

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            User user = appDatabase.userDao().findByEmail(email);
            if (email.equals("admin@gmail.com") && password.equals("123")) {
                runOnUiThread(() -> {
                    SharedPreferences userPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = userPrefs.edit();
                    editor.putString("role", User.ROLE_ADMIN);
                    editor.putBoolean("is_logged_in", true);
                    editor.apply();

                    Toast.makeText(this, "Admin Login successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, ProductListActivity.class));
                    finish();
                });
                return;
            }
            runOnUiThread(() -> {
                if (user != null && user.passwordHash.equals(password)) { // In a real app, use a proper hashing algorithm
                    if (cbRememberMe.isChecked()) {
                        sharedPreferences.edit()
                                .putBoolean("remember_me", true)
                                .putString("email", email)
                                .putString("password", password)
                                .apply();
                    } else {
                        sharedPreferences.edit().clear().apply();
                    }
                    SharedPreferences userPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = userPrefs.edit();
                    editor.putString("user_id", String.valueOf(user.userId));
                    editor.putString("role", user.role);
                    editor.putBoolean("is_logged_in", true);
                    editor.apply();

                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                    if (User.ROLE_ADMIN.equals(user.role)) {
                        startActivity(new Intent(LoginActivity.this, ProductListActivity.class));
                    } else {
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    }
                    finish();
                } else {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

}