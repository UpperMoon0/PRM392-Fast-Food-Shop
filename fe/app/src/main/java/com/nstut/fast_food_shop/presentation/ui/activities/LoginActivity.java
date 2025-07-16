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
import android.util.Log;


import com.google.gson.Gson;
import com.nstut.fast_food_shop.R;
import com.nstut.fast_food_shop.data.remote.response.UserResponse;
import com.nstut.fast_food_shop.repository.UserRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    private EditText etEmail, etPassword;
    private CheckBox cbRememberMe;
    private SharedPreferences sharedPreferences;
    private UserRepository userRepository;
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

        userRepository = new UserRepository();
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

        userRepository.login(email, password).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("user_id", userResponse.getUser().getId());
                    editor.putString("user_name", userResponse.getUser().getName());
                    editor.putString("user_email", userResponse.getUser().getEmail());
                    editor.putString("user_role", userResponse.getUser().getRole());
                    editor.apply();

                    if (cbRememberMe.isChecked()) {
                        editor.putBoolean("remember_me", true);
                        editor.putString("email", email);
                        editor.putString("password", password);
                        editor.apply();
                    } else {
                        sharedPreferences.edit().clear().apply();
                    }

                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

}