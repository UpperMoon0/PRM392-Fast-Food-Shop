package com.nstut.fast_food_shop.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.nstut.fast_food_shop.data.models.User;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
        User findByEmail(String email);
    
        @Query("SELECT * FROM users WHERE user_id = :userId")
        User getUserById(String userId);
    }