package com.nstut.fast_food_shop.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.nstut.fast_food_shop.data.models.Category;
import com.nstut.fast_food_shop.data.models.CategoryWithProducts;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("SELECT * FROM categories")
    LiveData<List<Category>> getAllCategories();

    @Query("SELECT * FROM categories")
    List<Category> getAllCategoriesList();

    @Query("SELECT * FROM categories WHERE id = :id")
    LiveData<Category> getCategoryById(int id);

    @Query("SELECT * FROM categories WHERE name = :name")
    Category getCategoryByName(String name);

    @Transaction
    @Query("SELECT * FROM categories")
    LiveData<List<CategoryWithProducts>> getCategoriesWithProducts();

    @Transaction
    @Query("SELECT * FROM categories WHERE id = :categoryId")
    LiveData<CategoryWithProducts> getCategoryWithProducts(int categoryId);
}