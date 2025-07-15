package com.nstut.fast_food_shop.data.local.db;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nstut.fast_food_shop.model.CartItem;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    private static Gson gson = new Gson();

    @TypeConverter
    public static List<String> fromString(String value) {
        if (value == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<String> list) {
        return gson.toJson(list);
    }

    @TypeConverter
    public static String fromCartItemList(List<CartItem> cartItems) {
        return gson.toJson(cartItems);
    }

    @TypeConverter
    public static List<CartItem> toCartItemList(String cartItemsJson) {
        if (cartItemsJson == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<CartItem>>() {}.getType();
        return gson.fromJson(cartItemsJson, listType);
    }
}