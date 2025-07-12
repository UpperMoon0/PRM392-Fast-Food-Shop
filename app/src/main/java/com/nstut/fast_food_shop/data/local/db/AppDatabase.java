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
        public void onOpen(SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
        
        @Override
        public void onCreate(SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                CategoryDao categoryDao = INSTANCE.categoryDao();
                ProductDao productDao = INSTANCE.productDao();
                Category category = categoryDao.getCategoryByName("Burgers");
                if (category == null) {
                    Category newCategory = new Category("Burgers", "Juicy burgers", "https://cmx.weightwatchers.com/assets-proxy/weight-watchers/image/upload/v1594406683/visitor-site/prod/ca/burgers_mobile_my18jv");
                    long categoryId = categoryDao.insert(newCategory);
                    category = categoryDao.getCategoryByName("Burgers");
                }

                if (category != null) {
                    if (productDao.getProductsByCategory(category.getId()).isEmpty()) {
                        ProductRoom fishBurger = new ProductRoom();
                        fishBurger.name = "Fish Burger";
                        fishBurger.description = "A delicious fish burger.";
                        fishBurger.price = 5.99;
                        fishBurger.imageUrl = "https://bing.com/th?id=OSK.0de7650612ab63cc30e014b84ebd148d";
                        fishBurger.isAvailable = true;
                        long fishBurgerId = productDao.insert(fishBurger);
                        productDao.insertProductCategoryCrossRef(new ProductCategoryCrossRef((int) fishBurgerId, category.getId()));


                        ProductRoom beefBurger = new ProductRoom();
                        beefBurger.name = "Beef Burger";
                        beefBurger.description = "A classic beef burger.";
                        beefBurger.price = 6.99;
                        beefBurger.imageUrl = "https://staticcookist.akamaized.net/wp-content/uploads/sites/22/2021/09/beef-burger.jpg";
                        beefBurger.isAvailable = true;
                        long beefBurgerId = productDao.insert(beefBurger);
                        productDao.insertProductCategoryCrossRef(new ProductCategoryCrossRef((int) beefBurgerId, category.getId()));

                        ProductRoom chickenBurger = new ProductRoom();
                        chickenBurger.name = "Chicken Burger";
                        chickenBurger.description = "A tasty chicken burger.";
                        chickenBurger.price = 6.49;
                        chickenBurger.imageUrl = "https://th.bing.com/th/id/R.63698cc97cc5c4fe1d1ce5dddcaac706?rik=bvnSDVdMzgYJeQ&pid=ImgRaw&r=0";
                        chickenBurger.isAvailable = true;
                        long chickenBurgerId = productDao.insert(chickenBurger);
                        productDao.insertProductCategoryCrossRef(new ProductCategoryCrossRef((int) chickenBurgerId, category.getId()));
                    }
                }

                Category drinksCategory = categoryDao.getCategoryByName("Drinks");
                if (drinksCategory == null) {
                    Category newDrinks = new Category("Drinks", "Refreshing beverages", "https://images.unsplash.com/photo-1504674900247-0877df9cc836");
                    long drinksCategoryId = categoryDao.insert(newDrinks);
                    drinksCategory = categoryDao.getCategoryByName("Drinks");
                }

                if (drinksCategory != null) {
                    if (productDao.getProductsByCategory(drinksCategory.getId()).isEmpty()) {
                        ProductRoom cola = new ProductRoom();
                        cola.name = "Cola";
                        cola.description = "Classic cola drink.";
                        cola.price = 1.99;
                        cola.imageUrl = "https://www.coca-cola.com/content/dam/onexp/us/en/brands/coca-cola-original/en_coca-cola-original-taste-20-oz_750x750_v1.jpg/width1338.jpg";
                        cola.isAvailable = true;
                        long colaId = productDao.insert(cola);
                        productDao.insertProductCategoryCrossRef(new ProductCategoryCrossRef((int) colaId, drinksCategory.getId()));

                        ProductRoom orangeJuice = new ProductRoom();
                        orangeJuice.name = "Orange Juice";
                        orangeJuice.description = "Freshly squeezed orange juice.";
                        orangeJuice.price = 2.49;
                        orangeJuice.imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/05/Orangejuice.jpg/1200px-Orangejuice.jpg";
                        orangeJuice.isAvailable = true;
                        long ojId = productDao.insert(orangeJuice);
                        productDao.insertProductCategoryCrossRef(new ProductCategoryCrossRef((int) ojId, drinksCategory.getId()));

                        ProductRoom lemonade = new ProductRoom();
                        lemonade.name = "Lemonade";
                        lemonade.description = "Cool and tangy lemonade.";
                        lemonade.price = 2.29;
                        lemonade.imageUrl = "https://assets.tmecosys.com/image/upload/t_web_rdp_recipe_584x480/img/recipe/ras/Assets/713FF3F9-C3C9-4213-96D6-403FAD1E94C2/Derivates/33352ab5-d113-41a0-ab33-2e3d8467a145.jpg";
                        lemonade.isAvailable = true;
                        long lemonadeId = productDao.insert(lemonade);
                        productDao.insertProductCategoryCrossRef(new ProductCategoryCrossRef((int) lemonadeId, drinksCategory.getId()));
                    }
                }
            });
        }
    };
}
