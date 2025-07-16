package com.nstut.fast_food_shop.service;

import com.nstut.fast_food_shop.dto.LoginRequest;
import com.nstut.fast_food_shop.dto.UserResponse;
import com.nstut.fast_food_shop.model.User;
import com.nstut.fast_food_shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElse(null);
        if (user != null && user.getPassword().equals(loginRequest.getPassword())) {
            UserResponse userResponse = new UserResponse();
            userResponse.setId(String.valueOf(user.getId()));
            userResponse.setName(user.getName());
            userResponse.setEmail(user.getEmail());
            userResponse.setRole(user.getRole());
            return userResponse;
        }
        return null;
    }

    @Override
    public User register(User user) {
        return userRepository.save(user);
    }
}