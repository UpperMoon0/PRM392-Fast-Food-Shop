package com.nstut.fast_food_shop.service;

import com.nstut.fast_food_shop.model.User;
import com.nstut.fast_food_shop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User getUserById(String id) {
        return userRepository.findById(Long.parseLong(id)).orElse(null);
    }
}