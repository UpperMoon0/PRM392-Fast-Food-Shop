package com.nstut.fast_food_shop.data.remote.request;

import java.math.BigDecimal;
import java.util.List;

public class ProductDTO {
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private List<Long> categoryIds;
    private boolean available;

    public ProductDTO(String name, String description, BigDecimal price, String imageUrl, List<Long> categoryIds, boolean available) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.categoryIds = categoryIds;
        this.available = available;
    }
}