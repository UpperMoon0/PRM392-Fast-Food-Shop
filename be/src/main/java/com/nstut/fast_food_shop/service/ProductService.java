package com.nstut.fast_food_shop.service;

import com.nstut.fast_food_shop.dto.ProductDTO;
import com.nstut.fast_food_shop.model.Product;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    List<Product> searchProducts(String keyword, Long categoryId);
    Product getProductById(String id);
    Product createProduct(ProductDTO productDTO);
    Product updateProduct(String id, ProductDTO productDTO);
    void deleteProduct(String id);
}