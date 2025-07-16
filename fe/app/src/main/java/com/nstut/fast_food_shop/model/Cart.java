package com.nstut.fast_food_shop.model;

import java.util.List;

public class Cart {
    private List<CartItem> items;
    private String userId;

    public Cart(List<CartItem> items, String userId) {
        this.items = items;
        this.userId = userId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}