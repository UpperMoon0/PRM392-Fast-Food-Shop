package com.nstut.fast_food_shop.repository;

import com.nstut.fast_food_shop.data.remote.ApiClient;
import com.nstut.fast_food_shop.data.remote.ApiService;
import com.nstut.fast_food_shop.model.DailyRevenue;
import com.nstut.fast_food_shop.model.Order;

import java.util.List;

import retrofit2.Call;

public class OrderRepository {

    private ApiService apiService;

    public OrderRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public Call<Order> createOrder(Order order) {
        return apiService.createOrder(order);
    }

    public Call<List<Order>> getOrdersByUser(String userId) {
        return apiService.getOrdersByUser(userId);
    }

    public Call<List<Order>> getAllOrders() {
        return apiService.getAllOrders();
    }

    public Call<List<DailyRevenue>> getDailyRevenue() {
        return apiService.getDailyRevenue();
    }
}