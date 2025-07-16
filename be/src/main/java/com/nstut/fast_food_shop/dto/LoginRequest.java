package com.nstut.fast_food_shop.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}