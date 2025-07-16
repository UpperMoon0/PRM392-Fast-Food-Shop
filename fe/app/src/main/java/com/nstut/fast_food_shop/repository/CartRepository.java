package com.nstut.fast_food_shop.repository;

import com.nstut.fast_food_shop.data.remote.ApiClient;
import com.nstut.fast_food_shop.data.remote.ApiService;
import com.nstut.fast_food_shop.data.remote.request.AddItemRequest;
import com.nstut.fast_food_shop.data.remote.request.UpdateItemRequest;
import com.nstut.fast_food_shop.model.Cart;

import retrofit2.Call;

public class CartRepository {

    private ApiService apiService;

    public CartRepository() {
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

    public Call<Cart> updateCartItem(String userId, String cartItemId, int quantity) {
        return apiService.updateCartItem(userId, cartItemId, new UpdateItemRequest(quantity));
    }

}
