package com.nstut.fast_food_shop.data.models;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;
import java.util.List;

public class CategoryWithProducts {
    @Embedded public Category category;
    @Relation(
            parentColumn = "id",
            entityColumn = "productId",
            associateBy = @Junction(
                    value = ProductCategoryCrossRef.class,
                    parentColumn = "categoryId",
                    entityColumn = "productId"
            )
    )
    public List<ProductRoom> products;
}