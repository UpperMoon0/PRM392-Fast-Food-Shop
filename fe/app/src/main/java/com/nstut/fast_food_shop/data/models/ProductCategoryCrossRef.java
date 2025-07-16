package com.nstut.fast_food_shop.data.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "product_category_cross_ref",
        primaryKeys = {"productId", "categoryId"},
        foreignKeys = {
                @ForeignKey(entity = ProductRoom.class,
                        parentColumns = "productId",
                        childColumns = "productId"),
                @ForeignKey(entity = Category.class,
                        parentColumns = "id",
                        childColumns = "categoryId")
        },
        indices = {@Index(value = "categoryId")})
public class ProductCategoryCrossRef {
    public int productId;
    public int categoryId;

    public ProductCategoryCrossRef(int productId, int categoryId) {
        this.productId = productId;
        this.categoryId = categoryId;
    }
}