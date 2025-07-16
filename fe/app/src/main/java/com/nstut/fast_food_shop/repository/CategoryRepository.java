package com.nstut.fast_food_shop.repository;

import com.nstut.fast_food_shop.data.remote.ApiClient;
import com.nstut.fast_food_shop.data.remote.ApiService;
import com.nstut.fast_food_shop.model.Category;

import java.util.List;

import retrofit2.Call;

public class CategoryRepository {

    private ApiService apiService;

    public CategoryRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public Call<List<Category>> getAllCategories() {
        return apiService.getAllCategories();
    }

    public Call<Category> getCategoryById(String id) {
        return apiService.getCategoryById(id);
    }

    public Call<Category> createCategory(Category category) {
        return apiService.createCategory(category);
    }

    public Call<Category> updateCategory(String id, Category category) {
        return apiService.updateCategory(id, category);
    }

    public Call<Void> deleteCategory(String id) {
        return apiService.deleteCategory(id);
    }
}