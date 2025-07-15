package com.nstut.fast_food_shop.data.local.db;

import com.nstut.fast_food_shop.data.local.dao.CategoryDao;
import com.nstut.fast_food_shop.data.local.dao.ProductDao;
import com.nstut.fast_food_shop.data.local.dao.UserDao;
import com.nstut.fast_food_shop.data.models.Category;
import com.nstut.fast_food_shop.data.models.ProductCategoryCrossRef;
import com.nstut.fast_food_shop.data.models.ProductRoom;
import com.nstut.fast_food_shop.data.models.User;
import com.nstut.fast_food_shop.presentation.utils.HashUtils;

public class DataInitializer {

    public static void initialize(AppDatabase appDatabase) {
        UserDao userDao = appDatabase.userDao();
        CategoryDao categoryDao = appDatabase.categoryDao();
        ProductDao productDao = appDatabase.productDao();

        // Initialize Admin User
        if (userDao.findByEmail("admin@gmail.com") == null) {
            User admin = new User();
            admin.email = "admin@gmail.com";
            admin.passwordHash = HashUtils.hashPassword("123");
            admin.role = User.ROLE_ADMIN;
            userDao.insert(admin);
        }

        // Initialize Categories and Products
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
            Category newDrinks = new Category("Drinks", "Refreshing beverages", "https://th.bing.com/th/id/R.07b5da6c4a6feef0faecde10979c4549?rik=pvfT%2b2%2fkQHq8gg&riu=http%3a%2f%2fwww.magazinediscover.com%2fwp-content%2fuploads%2f2018%2f01%2fbatidas.jpg&ehk=nm4nUnqdxzDB7HaveYXazcXRvCcqev1aK9Nw%2fdwEPIg%3d&risl=&pid=ImgRaw&r=0");
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
    }
}