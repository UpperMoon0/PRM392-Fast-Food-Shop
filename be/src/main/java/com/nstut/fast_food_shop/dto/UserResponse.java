package com.nstut.fast_food_shop.dto;

import lombok.Data;

@Data
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private String role;
}