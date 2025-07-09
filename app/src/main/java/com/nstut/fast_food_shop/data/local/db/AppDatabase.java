package com.nstut.fast_food_shop.data.local.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import androidx.sqlite.db.SupportSQLiteDatabase;
import com.nstut.fast_food_shop.data.local.dao.ProductDao;
import com.nstut.fast_food_shop.data.local.dao.UserDao;
import com.nstut.fast_food_shop.data.models.ProductRoom;
import com.nstut.fast_food_shop.data.models.User;
import java.util.concurrent.Executors;

@Database(entities = {ProductRoom.class, User.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract ProductDao productDao();
    public abstract UserDao userDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "product_db")
                    .fallbackToDestructiveMigration()
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate( SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadExecutor().execute(() -> {
                                User admin = new User();
                                admin.email = "admin@gmail.com";
                                admin.passwordHash = "123";
                                admin.fullName = "Admin";
                                admin.role = User.ROLE_ADMIN;
                                admin.createdAt = System.currentTimeMillis();
                                admin.updatedAt = System.currentTimeMillis();
                                INSTANCE.userDao().insert(admin);
                            });
                        }
                    })
                    .build();
        }
        return INSTANCE;
    }
}
