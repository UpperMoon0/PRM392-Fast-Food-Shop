package com.nstut.fast_food_shop.dto;

import lombok.Data;

@Data
public class ProductDTO {
    private String id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
}