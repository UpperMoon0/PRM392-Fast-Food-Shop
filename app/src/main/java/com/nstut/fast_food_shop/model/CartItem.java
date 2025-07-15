package com.nstut.fast_food_shop.model;

import com.nstut.fast_food_shop.data.models.ProductRoom;

public class CartItem {
    private ProductRoom product;
    private int quantity;

    public CartItem(ProductRoom product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public ProductRoom getProduct() {
        return product;
    }

    public void setProduct(ProductRoom product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}