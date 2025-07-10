package com.nstut.fast_food_shop.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.nstut.fast_food_shop.data.models.ProductRoom;

import java.util.List;

@Dao
public interface ProductDao {
    @Insert
    long insert(ProductRoom product);

    @Query("SELECT * FROM products")
    List<ProductRoom> getAll();

    @Update
    int update(ProductRoom product);

    @Delete
    int delete(ProductRoom product);

    @Query("SELECT * FROM products WHERE productId = :id")
    ProductRoom getById(int id);

    @Query("SELECT * FROM products WHERE isAvailable = 1")
    List<ProductRoom> getAllAvailable();

    @Query("SELECT * FROM products WHERE categoryId = :categoryId")
    List<ProductRoom> getProductsByCategory(int categoryId);
}
