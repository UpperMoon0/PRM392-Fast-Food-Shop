package com.nstut.fast_food_shop.service;

import com.nstut.fast_food_shop.model.Category;
import com.nstut.fast_food_shop.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(String id) {
        return categoryRepository.findById(Long.parseLong(id)).orElse(null);
    }

    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(String id, Category category) {
        if (categoryRepository.existsById(Long.parseLong(id))) {
            category.setId(Long.parseLong(id));
            return categoryRepository.save(category);
        }
        return null;
    }

    @Override
    public void deleteCategory(String id) {
        categoryRepository.deleteById(Long.parseLong(id));
    }
}