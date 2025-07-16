package com.nstut.fast_food_shop.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nstut.fast_food_shop.model.CartItem;
import com.nstut.fast_food_shop.data.models.ProductRoom;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartRepository {

    private static final String PREF_NAME = "CartPrefs";
    private static final String KEY_CART_ITEMS = "CartItems";
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public CartRepository(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public List<CartItem> getCartItems() {
        String json = sharedPreferences.getString(KEY_CART_ITEMS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<CartItem>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveCartItems(List<CartItem> cartItems) {
        String json = gson.toJson(cartItems);
        sharedPreferences.edit().putString(KEY_CART_ITEMS, json).apply();
    }

    public void addItemToCart(ProductRoom product, int quantity) {
        List<CartItem> cartItems = getCartItems();
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == (product.getId())) {
                item.setQuantity(item.getQuantity() + quantity);
                saveCartItems(cartItems);
                return;
            }
        }
        cartItems.add(new CartItem(product, quantity));
        saveCartItems(cartItems);
    }

    public void updateCartItem(CartItem cartItem) {
        List<CartItem> cartItems = getCartItems();
        for (int i = 0; i < cartItems.size(); i++) {
            if (cartItems.get(i).getProduct().getId() == cartItem.getProduct().getId()) {
                cartItems.set(i, cartItem);
                saveCartItems(cartItems);
                return;
            }
        }
    }

    public void removeCartItem(CartItem cartItem) {
        List<CartItem> cartItems = getCartItems();
        for (int i = 0; i < cartItems.size(); i++) {
            if (cartItems.get(i).getProduct().getId() == cartItem.getProduct().getId()) {
                cartItems.remove(i);
                saveCartItems(cartItems);
                return;
            }
        }
    }

    public void clearCart() {
        sharedPreferences.edit().remove(KEY_CART_ITEMS).apply();
    }
}
