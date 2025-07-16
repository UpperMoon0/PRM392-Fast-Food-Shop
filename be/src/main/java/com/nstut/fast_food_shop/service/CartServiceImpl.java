package com.nstut.fast_food_shop.service;

import com.nstut.fast_food_shop.model.Cart;
import com.nstut.fast_food_shop.model.CartItem;
import com.nstut.fast_food_shop.model.Product;
import com.nstut.fast_food_shop.model.User;
import com.nstut.fast_food_shop.repository.CartItemRepository;
import com.nstut.fast_food_shop.repository.CartRepository;
import com.nstut.fast_food_shop.repository.ProductRepository;
import com.nstut.fast_food_shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Cart getCartByUserId(String userId) {
        return cartRepository.findByCustomerId(Long.parseLong(userId)).orElse(null);
    }

    @Override
    public Cart addItemToCart(String userId, String productId, int quantity) {
        Cart cart = getCartByUserId(userId);
        if (cart == null) {
            User user = userRepository.findById(Long.parseLong(userId)).orElse(null);
            if (user != null) {
                cart = new Cart();
                cart.setCustomer(user);
                cart.setCartItems(new ArrayList<>());
            } else {
                return null;
            }
        }

        Product product = productRepository.findById(Long.parseLong(productId)).orElse(null);
        if (product != null) {
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setCart(cart);
            cart.getCartItems().add(cartItem);
            cartItemRepository.save(cartItem);
            return cartRepository.save(cart);
        }
        return null;
    }

    @Override
    public Cart removeItemFromCart(String userId, String cartItemId) {
        Cart cart = getCartByUserId(userId);
        if (cart != null) {
            cart.getCartItems().removeIf(item -> item.getId().equals(Long.parseLong(cartItemId)));
            cartItemRepository.deleteById(Long.parseLong(cartItemId));
            return cartRepository.save(cart);
        }
        return null;
    }

    @Override
    public Cart updateCartItem(String userId, String cartItemId, int quantity) {
        Cart cart = getCartByUserId(userId);
        if (cart != null) {
            cart.getCartItems().stream()
                    .filter(item -> item.getId().equals(Long.parseLong(cartItemId)))
                    .findFirst()
                    .ifPresent(item -> {
                        item.setQuantity(quantity);
                        cartItemRepository.save(item);
                    });
            return cartRepository.save(cart);
        }
        return null;
    }
}