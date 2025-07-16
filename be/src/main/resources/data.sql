-- Populate users
INSERT INTO users (email, password, role, name) VALUES
('admin@gmail.com', '123', 'ADMIN', 'Admin'),
('customer@gmail.com', '123', 'USER', 'Customer');

-- Populate categories
INSERT INTO categories (name, description, image_url) VALUES
('Burgers', 'Our burgers are made from 100% pure beef, grilled to perfection and served on a freshly baked bun.', 'https://cmx.weightwatchers.com/assets-proxy/weight-watchers/image/upload/v1594406683/visitor-site/prod/ca/burgers_mobile_my18jv'),
('Drinks', 'Quench your thirst with our wide selection of refreshing beverages.', 'https://tse2.mm.bing.net/th/id/OIP.ZQHlKHkpIUpiQIYKKuiWxgHaE8?r=0&rs=1&pid=ImgDetMain&o=7&rm=3'),
('Sides', 'Complete your meal with our delicious sides.', 'https://hips.hearstapps.com/del.h-cdn.co/assets/16/21/1464303120-shot-1-133.jpg?crop=1xw:0.99975xh;center,top&resize=768:*'),
('Desserts', 'Satisfy your sweet tooth with our delectable desserts.', 'https://th.bing.com/th/id/R.5753ccfa7ba513e51ab99523cb06aa18?rik=Sh2Q5aJJdCySpw&riu=http%3a%2f%2ffullhdwall.com%2fwp-content%2fuploads%2f2017%2f01%2fBeautiful-Dessert-Food.jpg&ehk=awUFAeoYDQJ5hEN8b5Aq9BCaPSN2saO5iVlF6K28yGU%3d&risl=&pid=ImgRaw&r=0');

-- Populate products
-- Burgers
-- Add the new column `available` to the `products` table
ALTER TABLE products ADD COLUMN available BOOLEAN DEFAULT TRUE;

-- Populate products
-- Burgers
INSERT INTO products (name, description, price, image_url, category_id, available) VALUES
('Fish Burger', 'A crispy fish fillet with tangy tartar sauce and fresh lettuce, all in a soft bun. A taste of the ocean in every bite.', 5.99, 'https://bing.com/th?id=OSK.0de7650612ab63cc30e014b84ebd148d', 1, TRUE),
('Beef Burger', 'A juicy, flame-grilled beef patty with fresh tomatoes, onions, and our secret sauce. A timeless classic that never disappoints.', 6.99, 'https://staticcookist.akamaized.net/wp-content/uploads/sites/22/2021/09/beef-burger.jpg', 1, TRUE),
('Chicken Burger', 'A tender, grilled chicken breast with crispy lettuce and mayonnaise. A lighter but equally delicious option.', 6.49, 'https://th.bing.com/th/id/R.63698cc97cc5c4fe1d1ce5dddcaac706?rik=bvnSDVdMzgYJeQ&pid=ImgRaw&r=0', 1, TRUE),
('Cheese Burger', 'Our classic beef burger with a slice of melted American cheese. An irresistible combination for cheese lovers.', 7.49, 'https://img-s-msn-com.akamaized.net/tenant/amp/entityid/AA1Ipiau.img?w=720&h=846&m=6', 1, FALSE),
('Veggie Burger', 'A delicious plant-based patty with fresh vegetables and a special sauce. A great choice for vegetarians and veggie lovers.', 6.99, 'https://tse4.mm.bing.net/th/id/OIP.s07UsAo90pfNbIUBM-OTbAHaHa?r=0&rs=1&pid=ImgDetMain&o=7&rm=3', 1, TRUE);

-- Drinks
INSERT INTO products (name, description, price, image_url, category_id, available) VALUES
('Cola', 'The classic, bubbly cola that everyone loves. The perfect companion for any meal.', 1.99, 'https://www.sportsdestinations.com/sites/sportsdestinations.com/files/2023-06/Coke.jpg', 2, TRUE),
('Orange Juice', '100% pure and natural orange juice, packed with Vitamin C. A healthy and refreshing choice.', 2.49, 'https://upload.wikimedia.org/wikipedia/commons/thumb/0/05/Orangejuice.jpg/1200px-Orangejuice.jpg', 2, TRUE),
('Lemonade', 'A perfect balance of sweet and tangy, our lemonade is a refreshing treat for a hot day.', 2.29, 'https://assets.tmecosys.com/image/upload/t_web_rdp_recipe_584x480/img/recipe/ras/Assets/713FF3F9-C3C9-4213-96D6-403FAD1E94C2/Derivates/33352ab5-d113-41a0-ab33-2e3d8467a145.jpg', 2, TRUE),
('Iced Tea', 'Freshly brewed black tea, sweetened and served over ice. A classic and refreshing beverage.', 2.19, 'https://res.cloudinary.com/hksqkdlah/image/upload/ar_1:1,c_fill,dpr_2.0,f_auto,fl_lossy.progressive.strip_profile,g_faces:auto,q_auto:low,w_344/SFS_iced_tea_41_wzgpm8', 2, FALSE),
('Apple Juice', 'Sweet and crisp apple juice, made from the finest apples. A delightful drink for all ages.', 2.49, 'https://tse1.explicit.bing.net/th/id/OIP.lXWXGV94K9O7qpK35aNI-gHaFy?r=0&rs=1&pid=ImgDetMain&o=7&rm=3', 2, TRUE);

-- Sides
INSERT INTO products (name, description, price, image_url, category_id, available) VALUES
('Fries', 'Golden and crispy french fries, lightly salted. The perfect side for any burger.', 2.99, 'https://www.seriouseats.com/thmb/_BkW9V2wK3Zed-zQAETkRSJS8ac=/1500x1125/filters:fill(auto,1)/__opt__aboutcom__coeus__resources__content_migration__serious_eats__seriouseats.com__2018__04__20180309-french-fries-vicky-wasik-15-5a9844742c2446c7a7be9fbd41b6e27d.jpg', 3, TRUE),
('Onion Rings', 'Crispy, golden-brown onion rings. A savory and satisfying side dish.', 3.49, 'https://tse4.mm.bing.net/th/id/OIP.Vu1SGNA-B7Tu0DfrhDqc2AHaE8?r=0&rs=1&pid=ImgDetMain&o=7&rm=3', 3, TRUE),
('Salad', 'A fresh mix of greens, tomatoes, and cucumbers with your choice of dressing. A light and healthy option.', 4.99, 'https://th.bing.com/th/id/R.15d74f11083a5ad96a0713e6bba89720?rik=iim1PF0NUAcXRg&riu=http%3a%2f%2fs3.amazonaws.com%2fimg.mynetdiary.com%2fblog%2fpower-salads.jpg&ehk=t0epkigIWUdqJTMl3uK6WNo792Z%2fNAJy9sUQtjDUNhM%3d&risl=&pid=ImgRaw&r=0', 3, TRUE);

-- Desserts
INSERT INTO products (name, description, price, image_url, category_id, available) VALUES
('Ice Cream', 'Creamy and delicious vanilla ice cream. A simple yet satisfying dessert.', 2.99, 'https://tarateaspoon.com/wp-content/uploads/2021/06/Vanilla-Ice-Cream-cone-glass-sq.jpeg', 4, TRUE),
('Apple Pie', 'A warm, flaky crust filled with sweet, spiced apples. A comforting classic.', 3.49, 'https://tse4.mm.bing.net/th/id/OIP.xXtGJh85hhLVjc7xTQ4RnQHaLH?r=0&rs=1&pid=ImgDetMain&o=7&rm=3', 4, FALSE),
('Chocolate Cake', 'A rich and moist chocolate cake with a decadent fudge frosting. A chocolate lover''s dream.', 4.99, 'https://tse1.mm.bing.net/th/id/OIP.7bEK8zNR1hmj63EuvmzdYgHaLH?r=0&rs=1&pid=ImgDetMain&o=7&rm=3', 4, TRUE);