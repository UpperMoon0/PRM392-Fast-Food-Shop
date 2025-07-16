package com.nstut.fast_food_shop.presentation.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.data.local.db.AppDatabase;
import com.nstut.fast_food_shop.data.models.User;
import com.nstut.fast_food_shop.presentation.utils.HashUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword, etPhoneNumber;
    private AppDatabase appDatabase;
    private ExecutorService executorService;
    private Button loginLogoutButton;

    @Override
    protected void onResume() {
        super.onResume();
        checkUserLoginStatus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        Button btnRegister = findViewById(R.id.btn_register);
        TextView tvLogin = findViewById(R.id.tv_login);

        appDatabase = AppDatabase.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        btnRegister.setOnClickListener(v -> registerUser());

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });

        loginLogoutButton = findViewById(R.id.login_logout_button);
        loginLogoutButton.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
            if (sharedPreferences.getString("user_id", null) != null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("user_id");
                editor.apply();
                checkUserLoginStatus();
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            executorService.execute(() -> {
                User existingUser = appDatabase.userDao().findByEmail(email);
                if (existingUser != null) {
                    runOnUiThread(() -> Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show());
                    return;
                }

                User user = new User();
                user.fullName = fullName;
                user.email = email;
                user.passwordHash = HashUtils.hashPassword(password);
                user.phoneNumber = phoneNumber;
                user.role = User.ROLE_USER;
                user.createdAt = System.currentTimeMillis();
                user.updatedAt = System.currentTimeMillis();

                appDatabase.userDao().insert(user);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        });
    }

    private void checkUserLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        if (sharedPreferences.getString("user_id", null) != null) {
            loginLogoutButton.setText("Logout");
        } else {
            loginLogoutButton.setText("Login");
        }
    }
}