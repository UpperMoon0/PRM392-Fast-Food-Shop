package com.nstut.fast_food_shop.data.remote.response;

import com.nstut.fast_food_shop.model.User;

public class LoginResponse {
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}