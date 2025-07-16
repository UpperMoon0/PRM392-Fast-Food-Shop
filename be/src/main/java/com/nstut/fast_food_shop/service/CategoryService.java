package com.nstut.fast_food_shop.service;

import com.nstut.fast_food_shop.model.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(String id);
    Category createCategory(Category category);
    Category updateCategory(String id, Category category);
    void deleteCategory(String id);
}