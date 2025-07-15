package com.nstut.fast_food_shop.data.local.db;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.nstut.fast_food_shop.data.local.dao.CategoryDao;
import com.nstut.fast_food_shop.data.local.dao.ProductDao;
import com.nstut.fast_food_shop.data.local.dao.UserDao;
import com.nstut.fast_food_shop.data.models.Category;
import com.nstut.fast_food_shop.data.models.ProductCategoryCrossRef;
import com.nstut.fast_food_shop.data.models.ProductRoom;
import com.nstut.fast_food_shop.data.models.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {ProductRoom.class, User.class, Category.class, ProductCategoryCrossRef.class}, version = 12, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract ProductDao productDao();
    public abstract UserDao userDao();
    public abstract CategoryDao categoryDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "product_db")
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                DataInitializer.initialize(INSTANCE);
            });
        }
    };
}
