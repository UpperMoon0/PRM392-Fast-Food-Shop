package com.nstut.fast_food_shop.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.nstut.fast_food_shop.data.models.ProductCategoryCrossRef;
import com.nstut.fast_food_shop.data.models.ProductRoom;
import com.nstut.fast_food_shop.data.models.ProductWithCategories;

import java.util.List;

@Dao
public interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ProductRoom product);

    @Update
    void update(ProductRoom product);

    @Delete
    void delete(ProductRoom product);

    @Query("SELECT * FROM products")
    LiveData<List<ProductRoom>> getAllProducts();

    @Query("SELECT * FROM products WHERE productId = :id")
    LiveData<ProductRoom> getProductById(int id);

    @Query("SELECT * FROM products WHERE productId = :id")
    ProductRoom getById(int id);

    @Transaction
    @Query("SELECT * FROM products")
    LiveData<List<ProductWithCategories>> getProductsWithCategories();

    @Transaction
    @Query("SELECT * FROM products WHERE productId = :productId")
    LiveData<ProductWithCategories> getProductWithCategories(int productId);

    @Transaction
    @Query("SELECT * FROM products WHERE productId = :productId")
    ProductWithCategories getProductWithCategoriesById(int productId);

    @Query("SELECT p.* FROM products p INNER JOIN product_category_cross_ref ref ON p.productId = ref.productId WHERE ref.categoryId = :categoryId")
    List<ProductRoom> getProductsByCategory(int categoryId);

    @Transaction
    @Query("SELECT p.* FROM products p INNER JOIN product_category_cross_ref ref ON p.productId = ref.productId WHERE ref.categoryId = :categoryId")
    LiveData<List<ProductWithCategories>> getProductsWithCategoriesByCategoryId(int categoryId);

    @Transaction
    @Query("SELECT * FROM products WHERE isAvailable = 1")
    LiveData<List<ProductWithCategories>> getAvailableProductsWithCategories();


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProductCategoryCrossRef(ProductCategoryCrossRef crossRef);

    @Transaction
    public default void insertProductWithCategories(ProductRoom product, List<Integer> categoryIds) {
        long productId = insert(product);
        for (Integer categoryId : categoryIds) {
            ProductCategoryCrossRef crossRef = new ProductCategoryCrossRef((int) productId, categoryId);
            insertProductCategoryCrossRef(crossRef);
        }
    }

    @Transaction
    public default void updateProductWithCategories(ProductRoom product, List<Integer> categoryIds) {
        update(product);
        deleteProductCategoryCrossRef(product.getId());
        for (Integer categoryId : categoryIds) {
            ProductCategoryCrossRef crossRef = new ProductCategoryCrossRef(product.getId(), categoryId);
            insertProductCategoryCrossRef(crossRef);
        }
    }

    @Query("DELETE FROM product_category_cross_ref WHERE productId = :productId")
    void deleteProductCategoryCrossRef(int productId);

    @Transaction
    public default void deleteProduct(ProductRoom product) {
        deleteProductCategoryCrossRef(product.getId());
        delete(product);
    }
}
