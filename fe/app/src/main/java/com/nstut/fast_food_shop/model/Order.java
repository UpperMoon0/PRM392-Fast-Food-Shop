package com.nstut.fast_food_shop.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.nstut.fast_food_shop.data.local.db.Converters;

import java.util.Date;
import java.util.List;

@Entity(tableName = "orders")
public class Order {
    @PrimaryKey
    @NonNull
    private String orderId;
    private String userId;
    @TypeConverters(Converters.class)
    private List<CartItem> items;
    private double totalAmount;
    @TypeConverters(Converters.class)
    private Date orderDate;
    private String status;

    public Order() {
    }

    public Order(String orderId, String userId, List<CartItem> items, double totalAmount, Date orderDate, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}