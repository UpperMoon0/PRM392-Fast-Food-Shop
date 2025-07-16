package com.nstut.fast_food_shop.dto;

import com.nstut.fast_food_shop.model.User;
import lombok.Data;

import java.util.List;

@Data
public class OrderDTO {
    private String id;
    private User user;
    private List<OrderItemDTO> orderItems;
    private double total;
}