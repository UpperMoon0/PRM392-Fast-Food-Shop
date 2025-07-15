package com.nstut.fast_food_shop.data.models;

public class ChatMessage {
    private String message;
    private boolean isSentByUser;
    private boolean isRecommendation;
    private ProductRoom product;

    public ChatMessage(String message, boolean isSentByUser) {
        this.message = message;
        this.isSentByUser = isSentByUser;
        this.isRecommendation = false;
    }

    public ChatMessage(ProductRoom product) {
        this.product = product;
        this.isSentByUser = false;
        this.isRecommendation = true;
        this.message = "Here is a recommendation for you";
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

    public ProductRoom getProduct() {
        return product;
    }
}