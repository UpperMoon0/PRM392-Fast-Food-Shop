package com.nstut.fast_food_shop.data.local.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.nstut.fast_food_shop.data.local.dao.CategoryDao;
import com.nstut.fast_food_shop.data.local.dao.ProductDao;
import com.nstut.fast_food_shop.data.local.dao.UserDao;
import com.nstut.fast_food_shop.data.models.Category;
import com.nstut.fast_food_shop.data.models.ProductRoom;
import com.nstut.fast_food_shop.data.models.User;

@Database(entities = {ProductRoom.class, User.class, Category.class}, version = 5, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract ProductDao productDao();
    public abstract UserDao userDao();
    public abstract CategoryDao categoryDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "product_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
