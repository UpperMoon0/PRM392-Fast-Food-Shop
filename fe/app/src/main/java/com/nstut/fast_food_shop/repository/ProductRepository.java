package com.nstut.fast_food_shop.repository;

import com.nstut.fast_food_shop.data.remote.ApiClient;
import com.nstut.fast_food_shop.data.remote.ApiService;
import com.nstut.fast_food_shop.model.Product;

import java.util.List;

import retrofit2.Call;

public class ProductRepository {

    private ApiService apiService;

    public ProductRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public Call<List<Product>> getAllProducts() {
        return apiService.getAllProducts();
    }

    public Call<Product> getProductById(String id) {
        return apiService.getProductById(id);
    }

    public Call<Product> createProduct(Product product) {
        return apiService.createProduct(product);
    }

    public Call<Product> updateProduct(String id, Product product) {
        return apiService.updateProduct(id, product);
    }

    public Call<Void> deleteProduct(String id) {
        return apiService.deleteProduct(id);
    }
}