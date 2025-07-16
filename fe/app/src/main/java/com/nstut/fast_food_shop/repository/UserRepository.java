package com.nstut.fast_food_shop.repository;

import com.nstut.fast_food_shop.data.remote.ApiClient;
import com.nstut.fast_food_shop.data.remote.ApiService;
import com.nstut.fast_food_shop.data.remote.request.LoginRequest;
import com.nstut.fast_food_shop.data.remote.response.LoginResponse;
import com.nstut.fast_food_shop.model.User;

import retrofit2.Call;

public class UserRepository {

    private ApiService apiService;

    public UserRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public Call<LoginResponse> login(String email, String password) {
        return apiService.login(new LoginRequest(email, password));
    }

    public Call<User> register(User user) {
        return apiService.register(user);
    }

    public Call<User> getUserById(String id) {
        return apiService.getUserById(id);
    }
}