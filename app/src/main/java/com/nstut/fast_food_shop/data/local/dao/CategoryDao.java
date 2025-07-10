package com.nstut.fast_food_shop.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.nstut.fast_food_shop.data.models.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Category... categories);

    @Query("SELECT * FROM categories")
    List<Category> getAllCategories();
}