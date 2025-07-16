package com.nstut.fast_food_shop.service;

import com.nstut.fast_food_shop.dto.ProductDTO;
import com.nstut.fast_food_shop.model.Category;
import com.nstut.fast_food_shop.model.Product;
import com.nstut.fast_food_shop.repository.CategoryRepository;
import com.nstut.fast_food_shop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> searchProducts(String keyword, Long categoryId) {
        if (keyword != null && !keyword.isEmpty() && categoryId != null && categoryId != -1) {
            return productRepository.findByNameContainingIgnoreCaseAndCategoriesId(keyword, categoryId);
        } else if (keyword != null && !keyword.isEmpty()) {
            return productRepository.findByNameContainingIgnoreCase(keyword);
        } else if (categoryId != null && categoryId != -1) {
            return productRepository.findByCategoriesId(categoryId);
        } else {
            return productRepository.findAll();
        }
    }

    @Override
    public Product getProductById(String id) {
        return productRepository.findById(Long.parseLong(id)).orElse(null);
    }

    @Override
    public Product createProduct(ProductDTO productDTO) {
        Product product = new Product();
        updateProductFromDTO(product, productDTO);
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(String id, ProductDTO productDTO) {
        return productRepository.findById(Long.parseLong(id)).map(product -> {
            updateProductFromDTO(product, productDTO);
            return productRepository.save(product);
        }).orElse(null);
    }

    @Override
    public void deleteProduct(String id) {
        productRepository.deleteById(Long.parseLong(id));
    }

    private void updateProductFromDTO(Product product, ProductDTO productDTO) {
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setImageUrl(productDTO.getImageUrl());
        product.setAvailable(productDTO.isAvailable());
        if (productDTO.getCategoryIds() != null && !productDTO.getCategoryIds().isEmpty()) {
            List<Category> categories = productDTO.getCategoryIds().stream()
                    .map(categoryId -> categoryRepository.findById(categoryId).orElse(null))
                    .filter(category -> category != null)
                    .collect(Collectors.toList());
            product.setCategories(categories);
        }
    }
}