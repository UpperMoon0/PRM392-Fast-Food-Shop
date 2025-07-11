package com.nstut.fast_food_shop.repository;

import com.nstut.fast_food_shop.model.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class CartRepository {
    private static final List<FoodItem> cart = new ArrayList<>();

    public static List<FoodItem> getCart() {
        return cart;
    }

    public static void addItem(FoodItem item) {
        cart.add(item);
    }
}
