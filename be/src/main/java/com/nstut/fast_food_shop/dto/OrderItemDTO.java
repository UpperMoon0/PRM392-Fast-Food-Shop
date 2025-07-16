package com.nstut.fast_food_shop.dto;

import com.nstut.fast_food_shop.model.Product;
import lombok.Data;

@Data
public class OrderItemDTO {
    private String id;
    private Product product;
    private int quantity;
}