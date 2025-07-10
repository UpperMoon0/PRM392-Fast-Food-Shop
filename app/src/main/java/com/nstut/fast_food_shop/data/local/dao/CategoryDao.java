package com.nstut.fast_food_shop.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.nstut.fast_food_shop.data.models.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("SELECT * FROM categories")
    List<Category> getAllCategories();

    @Query("SELECT * FROM categories WHERE id = :id")
    Category getCategoryById(int id);
}