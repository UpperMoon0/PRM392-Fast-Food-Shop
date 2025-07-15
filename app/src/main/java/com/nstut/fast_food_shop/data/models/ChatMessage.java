package com.nstut.fast_food_shop.data.models;

import java.util.List;

public class ChatMessage {
    private String message;
    private boolean isSentByUser;
    private boolean isRecommendation;
    private List<ProductRoom> products;

    public ChatMessage(String message, boolean isSentByUser) {
        this.message = message;
        this.isSentByUser = isSentByUser;
        this.isRecommendation = false;
    }

    public ChatMessage(List<ProductRoom> products) {
        this.products = products;
        this.isSentByUser = false;
        this.isRecommendation = true;
        this.message = "Here are some recommendations for you";
    }

    public ChatMessage(String message, List<ProductRoom> products) {
        this.message = message;
        this.products = products;
        this.isSentByUser = false;
        this.isRecommendation = true;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSentByUser() {
        return isSentByUser;
    }

    public boolean isRecommendation() {
        return isRecommendation;
    }

    public List<ProductRoom> getProducts() {
        return products;
    }
}