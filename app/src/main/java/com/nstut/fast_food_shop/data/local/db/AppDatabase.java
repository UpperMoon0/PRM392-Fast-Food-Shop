package com.nstut.fast_food_shop.data.local.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.nstut.fast_food_shop.data.local.dao.CategoryDao;
import com.nstut.fast_food_shop.data.local.dao.ProductDao;
import com.nstut.fast_food_shop.data.local.dao.UserDao;
import com.nstut.fast_food_shop.data.models.Category;
import com.nstut.fast_food_shop.data.models.ProductRoom;
import com.nstut.fast_food_shop.data.models.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {ProductRoom.class, User.class, Category.class}, version = 6, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
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
                CategoryDao categoryDao = INSTANCE.categoryDao();
                ProductDao productDao = INSTANCE.productDao();
                Category category = categoryDao.getCategoryByName("Burgers");
                if (category == null) {
                    categoryDao.insert(new Category("Burgers", "Juicy burgers", "https://cmx.weightwatchers.com/assets-proxy/weight-watchers/image/upload/v1594406683/visitor-site/prod/ca/burgers_mobile_my18jv"));
                    category = categoryDao.getCategoryByName("Burgers");
                }

                if (productDao.getProductsByCategory(category.getId()).isEmpty()) {
                    ProductRoom fishBurger = new ProductRoom();
                    fishBurger.name = "Fish Burger";
                    fishBurger.description = "A delicious fish burger.";
                    fishBurger.price = 5.99;
                    fishBurger.imageUrl = "https://bing.com/th?id=OSK.0de7650612ab63cc30e014b84ebd148d";
                    fishBurger.categoryId = category.getId();
                    fishBurger.isAvailable = true;
                    productDao.insert(fishBurger);

                    ProductRoom beefBurger = new ProductRoom();
                    beefBurger.name = "Beef Burger";
                    beefBurger.description = "A classic beef burger.";
                    beefBurger.price = 6.99;
                    beefBurger.imageUrl = "https://staticcookist.akamaized.net/wp-content/uploads/sites/22/2021/09/beef-burger.jpg";
                    beefBurger.categoryId = category.getId();
                    beefBurger.isAvailable = true;
                    productDao.insert(beefBurger);

                    ProductRoom chickenBurger = new ProductRoom();
                    chickenBurger.name = "Chicken Burger";
                    chickenBurger.description = "A tasty chicken burger.";
                    chickenBurger.price = 6.49;
                    chickenBurger.imageUrl = "https://th.bing.com/th/id/R.63698cc97cc5c4fe1d1ce5dddcaac706?rik=bvnSDVdMzgYJeQ&pid=ImgRaw&r=0";
                    chickenBurger.categoryId = category.getId();
                    chickenBurger.isAvailable = true;
                    productDao.insert(chickenBurger);
                }
            });
        }
    };
}
