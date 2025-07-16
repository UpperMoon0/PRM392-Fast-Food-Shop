package com.nstut.fast_food_shop.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nstut.fast_food_shop.data.remote.ApiClient;
import com.nstut.fast_food_shop.data.remote.ApiService;
import com.nstut.fast_food_shop.data.remote.request.AddItemRequest;
import com.nstut.fast_food_shop.model.Cart;
import com.nstut.fast_food_shop.model.CartItem;
import com.nstut.fast_food_shop.model.Product;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class CartRepository {

    private ApiService apiService;

    public CartRepository(Context context) {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public Call<Cart> getCartByUserId(String userId) {
        return apiService.getCartByUserId(userId);
    }

    public Call<Cart> addItemToCart(String userId, String productId, int quantity) {
        return apiService.addItemToCart(userId, new AddItemRequest(productId, quantity));
    }

    public Call<Cart> removeItemFromCart(String userId, String cartItemId) {
        return apiService.removeItemFromCart(userId, cartItemId);
    }

}
