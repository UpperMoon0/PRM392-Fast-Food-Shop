package com.nstut.fast_food_shop.data.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "categories")
public class Category implements Serializable {
    @PrimaryKey
    @NonNull
    private String id;
    private String name;

    public Category(@NonNull String id, String name) {
        this.id = id;
        this.name = name;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}