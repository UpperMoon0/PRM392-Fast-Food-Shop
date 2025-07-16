package com.nstut.fast_food_shop.controller;

import com.nstut.fast_food_shop.model.Cart;
import com.nstut.fast_food_shop.service.CartService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCartByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<Cart> addItemToCart(@PathVariable String userId, @RequestBody AddItemRequest request) {
        return ResponseEntity.ok(cartService.addItemToCart(userId, request.getProductId(), request.getQuantity()));
    }

    @DeleteMapping("/{userId}/items/{cartItemId}")
    public ResponseEntity<Cart> removeItemFromCart(@PathVariable String userId, @PathVariable String cartItemId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(userId, cartItemId));
    }

    @PutMapping("/{userId}/items/{cartItemId}")
    public ResponseEntity<Cart> updateCartItem(@PathVariable String userId, @PathVariable String cartItemId, @RequestBody UpdateItemRequest request) {
        return ResponseEntity.ok(cartService.updateCartItem(userId, cartItemId, request.getQuantity()));
    }

    @Data
    static class AddItemRequest {
        private String productId;
        private int quantity;
    }

    @Data
    static class UpdateItemRequest {
        private int quantity;
    }
}