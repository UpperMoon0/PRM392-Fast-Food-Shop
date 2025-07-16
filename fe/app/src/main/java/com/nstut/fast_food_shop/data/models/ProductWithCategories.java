package com.nstut.fast_food_shop.data.models;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;
import java.util.List;

public class ProductWithCategories {
    @Embedded public ProductRoom product;
    @Relation(
            parentColumn = "productId",
            entityColumn = "id",
            associateBy = @Junction(
                    value = ProductCategoryCrossRef.class,
                    parentColumn = "productId",
                    entityColumn = "categoryId"
            )
    )
    public List<Category> categories;
}