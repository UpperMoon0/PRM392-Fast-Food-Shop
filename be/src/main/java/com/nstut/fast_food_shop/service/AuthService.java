package com.nstut.fast_food_shop.service;

import com.nstut.fast_food_shop.dto.LoginRequest;
import com.nstut.fast_food_shop.dto.UserResponse;
import com.nstut.fast_food_shop.model.User;

public interface AuthService {
    UserResponse login(LoginRequest loginRequest);
    User register(User user);
}