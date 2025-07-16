package com.nstut.fast_food_shop.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.nstut.fast_food_shop.model.Order;

import java.util.List;

@Dao
public interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrder(Order order);

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY orderDate DESC")
    List<Order> getOrdersByUserId(String userId);

    @Query("SELECT * FROM orders ORDER BY orderDate DESC")
    List<Order> getAllOrders();
}