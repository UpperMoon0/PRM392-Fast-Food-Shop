package com.nstut.fast_food_shop.service;

import com.nstut.fast_food_shop.model.Cart;

public interface CartService {
    Cart getCartByUserId(String userId);
    Cart addItemToCart(String userId, String productId, int quantity);
    Cart removeItemFromCart(String userId, String cartItemId);
    Cart updateCartItem(String userId, String cartItemId, int quantity);
}