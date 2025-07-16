package com.nstut.fast_food_shop.service;

import com.nstut.fast_food_shop.dto.LoginRequest;
import com.nstut.fast_food_shop.dto.LoginResponse;
import com.nstut.fast_food_shop.model.User;
import com.nstut.fast_food_shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getPassword().equals(loginRequest.getPassword())) {
            return new LoginResponse(user);
        }
        throw new RuntimeException("Invalid credentials");
    }

    @Override
    public User register(User user) {
        return userRepository.save(user);
    }
}