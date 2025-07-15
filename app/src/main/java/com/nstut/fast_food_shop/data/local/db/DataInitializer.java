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

        if (userDao.findByEmail("customer@gmail.com") == null) {
            User customer = new User();
            customer.email = "customer@gmail.com";
            customer.passwordHash = HashUtils.hashPassword("123");
            customer.role = User.ROLE_USER;
            userDao.insert(customer);
        }

        // Initialize Categories and Products
        Category category = categoryDao.getCategoryByName("Burgers");
        if (category == null) {
            Category newCategory = new Category("Burgers", "Our burgers are made from 100% pure beef, grilled to perfection and served on a freshly baked bun.", "https://cmx.weightwatchers.com/assets-proxy/weight-watchers/image/upload/v1594406683/visitor-site/prod/ca/burgers_mobile_my18jv");
            long categoryId = categoryDao.insert(newCategory);
            category = categoryDao.getCategoryByName("Burgers");
        }

        if (category != null) {
            if (productDao.getProductsByCategory(category.getId()).isEmpty()) {
                ProductRoom fishBurger = new ProductRoom();
                fishBurger.name = "Fish Burger";
                fishBurger.description = "A crispy fish fillet with tangy tartar sauce and fresh lettuce, all in a soft bun. A taste of the ocean in every bite.";
                fishBurger.price = 5.99;
                fishBurger.imageUrl = "https://bing.com/th?id=OSK.0de7650612ab63cc30e014b84ebd148d";
                fishBurger.isAvailable = true;
                long fishBurgerId = productDao.insert(fishBurger);
                productDao.insertProductCategoryCrossRef(new ProductCategoryCrossRef((int) fishBurgerId, category.getId()));

                ProductRoom beefBurger = new ProductRoom();
                beefBurger.name = "Beef Burger";
                beefBurger.description = "A juicy, flame-grilled beef patty with fresh tomatoes, onions, and our secret sauce. A timeless classic that never disappoints.";
                beefBurger.price = 6.99;
                beefBurger.imageUrl = "https://staticcookist.akamaized.net/wp-content/uploads/sites/22/2021/09/beef-burger.jpg";
                beefBurger.isAvailable = true;
                long beefBurgerId = productDao.insert(beefBurger);
                productDao.insertProductCategoryCrossRef(new ProductCategoryCrossRef((int) beefBurgerId, category.getId()));

                ProductRoom chickenBurger = new ProductRoom();
                chickenBurger.name = "Chicken Burger";
                chickenBurger.description = "A tender, grilled chicken breast with crispy lettuce and mayonnaise. A lighter but equally delicious option.";
                chickenBurger.price = 6.49;
                chickenBurger.imageUrl = "https://th.bing.com/th/id/R.63698cc97cc5c4fe1d1ce5dddcaac706?rik=bvnSDVdMzgYJeQ&pid=ImgRaw&r=0";
                chickenBurger.isAvailable = true;
                long chickenBurgerId = productDao.insert(chickenBurger);
                productDao.insertProductCategoryCrossRef(new ProductCategoryCrossRef((int) chickenBurgerId, category.getId()));

                ProductRoom cheeseBurger = new ProductRoom();
                cheeseBurger.name = "Cheese Burger";
                cheeseBurger.description = "Our classic beef burger with a slice of melted American cheese. An irresistible combination for cheese lovers.";
                cheeseBurger.price = 7.49;
                cheeseBurger.imageUrl = "https://img-s-msn-com.akamaized.net/tenant/amp/entityid/AA1Ipiau.img?w=720&h=846&m=6";
                cheeseBurger.isAvailable = true;
                long cheeseBurgerId = productDao.insert(cheeseBurger);
                productDao.insertProductCategoryCrossRef(new ProductCategoryCrossRef((int) cheeseBurgerId, category.getId()));

                ProductRoom veggieBurger = new ProductRoom();
                veggieBurger.name = "Veggie Burger";
                veggieBurger.description = "A delicious plant-based patty with fresh vegetables and a special sauce. A great choice for vegetarians and veggie lovers.";
                veggieBurger.price = 6.99;
                veggieBurger.imageUrl = "https://tse4.mm.bing.net/th/id/OIP.s07UsAo90pfNbIUBM-OTbAHaHa?r=0&rs=1&pid=ImgDetMain&o=7&rm=3";
                veggieBurger.isAvailable = true;
                long veggieBurgerId = productDao.insert(veggieBurger);
                productDao.insertProductCategoryCrossRef(new ProductCategoryCrossRef((int) veggieBurgerId, category.getId()));
            }
        }

        Category drinksCategory = categoryDao.getCategoryByName("Drinks");
        if (drinksCategory == null) {
            Category newDrinks = new Category("Drinks", "Quench your thirst with our wide selection of refreshing beverages.", "https://th.bing.com/th/id/R.07b5da6c4a6feef0faecde10979c4549?rik=pvfT%2b2%2fkQHq8gg&riu=http%3a%2f%2fwww.magazinediscover.com%2fwp-content%2fuploads%2f2018%2f01%2fbatidas.jpg&ehk=nm4nUnqdxzDB7HaveYXazcXRvCcqev1aK9Nw%2f%2fdwEPIg%3d&risl=&pid=ImgRaw&r=0");
            long drinksCategoryId = categoryDao.insert(newDrinks);
            drinksCategory = categoryDao.getCategoryByName("Drinks");
        }

        if (drinksCategory != null) {
            if (productDao.getProductsByCategory(drinksCategory.getId()).isEmpty()) {
                ProductRoom cola = new ProductRoom();
                cola.name = "Cola";
                cola.description = "The classic, bubbly cola that everyone loves. The perfect companion for any meal.";
                cola.price = 1.99;
                cola.imageUrl = "https://www.sportsdestinations.com/sites/sportsdestinations.com/files/2023-06/Coke.jpg";
                cola.isAvailable = true;
                long colaId = productDao.insert(cola);
                productDao.insertProductCategoryCrossRef(new ProductCategoryCrossRef((int) colaId, drinksCategory.getId()));

                ProductRoom orangeJuice = new ProductRoom();
                orangeJuice.name = "Orange Juice";
                orangeJuice.description = "100% pure and natural orange juice, packed with Vitamin C. A healthy and refreshing choice.";
                orangeJuice.price = 2.49;
                orangeJuice.imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/05/Orangejuice.jpg/1200px-Orangejuice.jpg";
                orangeJuice.isAvailable = true;
                long ojId = productDao.insert(orangeJuice);
                productDao.insertProductCategoryCrossRef(new ProductCategoryCrossRef((int) ojId, drinksCategory.getId()));

                ProductRoom lemonade = new ProductRoom();
                lemonade.name = "Lemonade";
                lemonade.description = "A perfect balance of sweet and tangy, our lemonade is a refreshing treat for a hot day.";
                lemonade.price = 2.29;
                lemonade.imageUrl = "https://assets.tmecosys.com/image/upload/t_web_rdp_recipe_584x480/img/recipe/ras/Assets/713FF3F9-C3C9-4213-96D6-403FAD1E94C2/Derivates/33352ab5-d113-41a0-ab33-2e3d8467a145.jpg";
                lemonade.isAvailable = true;
                long lemonadeId = productDao.insert(lemonade);
                productDao.insertProductCategoryCrossRef(new ProductCategoryCrossRef((int) lemonadeId, drinksCategory.getId()));

                ProductRoom icedTea = new ProductRoom();
                icedTea.name = "Iced Tea";
                icedTea.description = "Freshly brewed black tea, sweetened and served over ice. A classic and refreshing beverage.";
                icedTea.price = 2.19;
                icedTea.imageUrl = "https://res.cloudinary.com/hksqkdlah/image/upload/ar_1:1,c_fill,dpr_2.0,f_auto,fl_lossy.progressive.strip_profile,g_faces:auto,q_auto:low,w_344/SFS_iced_tea_41_wzgpm8";
                icedTea.isAvailable = true;
                long icedTeaId = productDao.insert(icedTea);
                productDao.insertProductCategoryCrossRef(new ProductCategoryCrossRef((int) icedTeaId, drinksCategory.getId()));

                ProductRoom appleJuice = new ProductRoom();
                appleJuice.name = "Apple Juice";
                appleJuice.description = "Sweet and crisp apple juice, made from the finest apples. A delightful drink for all ages.";
                appleJuice.price = 2.49;
                appleJuice.imageUrl = "https://tse1.explicit.bing.net/th/id/OIP.lXWXGV94K9O7qpK35aNI-gHaFy?r=0&rs=1&pid=ImgDetMain&o=7&rm=3";
                appleJuice.isAvailable = true;
                long appleJuiceId = productDao.insert(appleJuice);
                productDao.insertProductCategoryCrossRef(new ProductCategoryCrossRef((int) appleJuiceId, drinksCategory.getId()));
            }
        }

        Category sidesCategory = categoryDao.getCategoryByName("Sides");
        if (sidesCategory == null) {
            Category newSides = new Category("Sides", "Complete your meal with our delicious sides.", "https://hips.hearstapps.com/del.h-cdn.co/assets/16/21/1464303120-shot-1-133.jpg?crop=1xw:0.99975xh;center,top&resize=768:*");
            long sidesCategoryId = categoryDao.insert(newSides);
            sidesCategory = categoryDao.getCategoryByName("Sides");
        }

        if (sidesCategory != null) {
            if (productDao.getProductsByCategory(sidesCategory.getId()).isEmpty()) {
                ProductRoom fries = new ProductRoom();
                fries.name = "Fries";
                fries.description = "Golden and crispy french fries, lightly salted. The perfect side for any burger.";
                fries.price = 2.99;
                fries.imageUrl = "https://www.seriouseats.com/thmb/_BkW9V2wK3Zed-zQAETkRSJS8ac=/1500x1125/filters:fill(auto,1)/__opt__aboutcom__coeus__resources__content_migration__serious_eats__seriouseats.com__2018__04__20180309-french-fries-vicky-wasik-15-5a9844742c2446c7a7be9fbd41b6e27d.jpg";
                fries.isAvailable = true;
                long friesId = productDao.insert(fries);
                productDao.insertProductCategoryCrossRef(new ProductCategoryCrossRef((int) friesId, sidesCategory.getId()));

                ProductRoom onionRings = new ProductRoom();
                onionRings.name = "Onion Rings";
                onionRings.description = "Crispy, golden-brown onion rings. A savory and satisfying side dish.";
                onionRings.price = 3.49;
                onionRings.imageUrl = "https://tse4.mm.bing.net/th/id/OIP.Vu1SGNA-B7Tu0DfrhDqc2AHaE8?r=0&rs=1&pid=ImgDetMain&o=7&rm=3";
                onionRings.isAvailable = true;
                long onionRingsId = productDao.insert(onionRings);
                productDao.insertProductCategoryCrossRef(new ProductCategoryCrossRef((int) onionRingsId, sidesCategory.getId()));

                ProductRoom salad = new ProductRoom();
                salad.name = "Salad";
                salad.description = "A fresh mix of greens, tomatoes, and cucumbers with your choice of dressing. A light and healthy option.";
                salad.price = 4.99;
                salad.imageUrl = "https://th.bing.com/th/id/R.15d74f11083a5ad96a0713e6bba89720?rik=iim1PF0NUAcXRg&riu=http%3a%2f%2fs3.amazonaws.com%2fimg.mynetdiary.com%2fblog%2fpower-salads.jpg&ehk=t0epkigIWUdqJTMl3uK6WNo792Z%2fNAJy9sUQtjDUNhM%3d&risl=&pid=ImgRaw&r=0";
                salad.isAvailable = true;
                long saladId = productDao.insert(salad);
                productDao.insertProductCategoryCrossRef(new ProductCategoryCrossRef((int) saladId, sidesCategory.getId()));
            }
        }

        Category dessertsCategory = categoryDao.getCategoryByName("Desserts");
        if (dessertsCategory == null) {
            Category newDesserts = new Category("Desserts", "Satisfy your sweet tooth with our delectable desserts.", "https://th.bing.com/th/id/R.5753ccfa7ba513e51ab99523cb06aa18?rik=Sh2Q5aJJdCySpw&riu=http%3a%2f%2ffullhdwall.com%2fwp-content%2fuploads%2f2017%2f01%2fBeautiful-Dessert-Food.jpg&ehk=awUFAeoYDQJ5hEN8b5Aq9BCaPSN2saO5iVlF6K28yGU%3d&risl=&pid=ImgRaw&r=0");
            long dessertsCategoryId = categoryDao.insert(newDesserts);
            dessertsCategory = categoryDao.getCategoryByName("Desserts");
        }

        if (dessertsCategory != null) {
            if (productDao.getProductsByCategory(dessertsCategory.getId()).isEmpty()) {
                ProductRoom iceCream = new ProductRoom();
                iceCream.name = "Ice Cream";
                iceCream.description = "Creamy and delicious vanilla ice cream. A simple yet satisfying dessert.";
                iceCream.price = 2.99;
                iceCream.imageUrl = "https://tarateaspoon.com/wp-content/uploads/2021/06/Vanilla-Ice-Cream-cone-glass-sq.jpeg";
                iceCream.isAvailable = true;
                long iceCreamId = productDao.insert(iceCream);
                productDao.insertProductCategoryCrossRef(new ProductCategoryCrossRef((int) iceCreamId, dessertsCategory.getId()));

                ProductRoom applePie = new ProductRoom();
                applePie.name = "Apple Pie";
                applePie.description = "A warm, flaky crust filled with sweet, spiced apples. A comforting classic.";
                applePie.price = 3.49;
                applePie.imageUrl = "https://tse4.mm.bing.net/th/id/OIP.xXtGJh85hhLVjc7xTQ4RnQHaLH?r=0&rs=1&pid=ImgDetMain&o=7&rm=3";
                applePie.isAvailable = true;
                long applePieId = productDao.insert(applePie);
                productDao.insertProductCategoryCrossRef(new ProductCategoryCrossRef((int) applePieId, dessertsCategory.getId()));

                ProductRoom chocolateCake = new ProductRoom();
                chocolateCake.name = "Chocolate Cake";
                chocolateCake.description = "A rich and moist chocolate cake with a decadent fudge frosting. A chocolate lover's dream.";
                chocolateCake.price = 4.99;
                chocolateCake.imageUrl = "https://tse1.mm.bing.net/th/id/OIP.7bEK8zNR1hmj63EuvmzdYgHaLH?r=0&rs=1&pid=ImgDetMain&o=7&rm=3";
                chocolateCake.isAvailable = true;
                long chocolateCakeId = productDao.insert(chocolateCake);
                productDao.insertProductCategoryCrossRef(new ProductCategoryCrossRef((int) chocolateCakeId, dessertsCategory.getId()));
            }
        }
    }
}