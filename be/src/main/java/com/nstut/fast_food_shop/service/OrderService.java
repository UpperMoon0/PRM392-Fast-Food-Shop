package com.nstut.fast_food_shop.service;

import com.nstut.fast_food_shop.model.Order;

import java.util.List;

public interface OrderService {
    Order createOrder(Order order);
    List<Order> getOrdersByUser(String userId);
}