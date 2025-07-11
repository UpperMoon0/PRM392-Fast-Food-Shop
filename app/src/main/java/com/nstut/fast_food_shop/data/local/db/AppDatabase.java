package com.nstut.fast_food_shop.data.local.db;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
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

@Database(entities = {ProductRoom.class, User.class, Category.class, ProductCategoryCrossRef.class}, version = 10, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract ProductDao productDao();
    public abstract UserDao userDao();
    public abstract CategoryDao categoryDao();

    public static AppDatabase getInstance(Context context) {
        Log.d("DB_INIT", "getInstance called.");
        if (INSTANCE == null) {
            Log.d("DB_INIT", "INSTANCE is null, entering synchronized block.");
            synchronized (AppDatabase.class) {
                Log.d("DB_INIT", "Inside synchronized block.");
                if (INSTANCE == null) {
                    Log.d("DB_INIT", "INSTANCE is still null, building new database.");
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "product_db")
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                    Log.d("DB_INIT", "Database instance created.");
                } else {
                    Log.d("DB_INIT", "INSTANCE was created by another thread.");
                }
            }
        } else {
            Log.d("DB_INIT", "Returning existing INSTANCE.");
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(SupportSQLiteDatabase db) {
            super.onOpen(db);
            Log.d("DB_INIT", "Database opened.");
        }
        
        @Override
        public void onCreate(SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                Log.d("DB_INIT", "Database creation callback triggered.");
                CategoryDao categoryDao = INSTANCE.categoryDao();
                ProductDao productDao = INSTANCE.productDao();
                Category category = categoryDao.getCategoryByName("Burgers");
                if (category == null) {
                    Log.d("DB_INIT", "'Burgers' category not found. Creating it.");
                    Category newCategory = new Category("Burgers", "Juicy burgers", "https://cmx.weightwatchers.com/assets-proxy/weight-watchers/image/upload/v1594406683/visitor-site/prod/ca/burgers_mobile_my18jv");
                    long categoryId = categoryDao.insert(newCategory);
                    Log.d("DB_INIT", "Category created with ID: " + categoryId);
                    // Re-fetch the category to ensure we have the instance with the ID
                    category = categoryDao.getCategoryByName("Burgers");
                } else {
                    Log.d("DB_INIT", "'Burgers' category found with ID: " + category.getId());
                }

                // Seed products if the category exists and has no products
                if (category != null) {
                    if (productDao.getProductsByCategory(category.getId()).isEmpty()) {
                        Log.d("DB_INIT", "No products found for 'Burgers' category. Seeding products.");
                        ProductRoom fishBurger = new ProductRoom();
                        fishBurger.name = "Fish Burger";
                        fishBurger.description = "A delicious fish burger.";
                        fishBurger.price = 5.99;
                        fishBurger.imageUrl = "https://bing.com/th?id=OSK.0de7650612ab63cc30e014b84ebd148d";
                        fishBurger.isAvailable = true;
                        productDao.insert(fishBurger);

                        ProductRoom beefBurger = new ProductRoom();
                        beefBurger.name = "Beef Burger";
                        beefBurger.description = "A classic beef burger.";
                        beefBurger.price = 6.99;
                        beefBurger.imageUrl = "https://staticcookist.akamaized.net/wp-content/uploads/sites/22/2021/09/beef-burger.jpg";
                        beefBurger.isAvailable = true;
                        productDao.insert(beefBurger);

                        ProductRoom chickenBurger = new ProductRoom();
                        chickenBurger.name = "Chicken Burger";
                        chickenBurger.description = "A tasty chicken burger.";
                        chickenBurger.price = 6.49;
                        chickenBurger.imageUrl = "https://th.bing.com/th/id/R.63698cc97cc5c4fe1d1ce5dddcaac706?rik=bvnSDVdMzgYJeQ&pid=ImgRaw&r=0";
                        chickenBurger.isAvailable = true;
                        productDao.insert(chickenBurger);
                        Log.d("DB_INIT", "Finished seeding products.");
                    } else {
                        Log.d("DB_INIT", "Products for 'Burgers' category already exist.");
                    }
                } else {
                    Log.e("DB_INIT", "Failed to create or find 'Burgers' category. Cannot seed products.");
                }
            });
        }
    };
}
