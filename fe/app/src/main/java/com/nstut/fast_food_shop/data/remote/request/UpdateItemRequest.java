package com.nstut.fast_food_shop.data.remote.request;

public class UpdateItemRequest {
    private int quantity;

    public UpdateItemRequest(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}