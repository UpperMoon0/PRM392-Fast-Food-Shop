package com.nstut.fast_food_shop.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartDTO {
    private String id;
    private List<CartItemDTO> cartItems;
    private double total;
}