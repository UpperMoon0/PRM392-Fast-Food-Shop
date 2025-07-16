package com.nstut.fast_food_shop.repository;

import com.nstut.fast_food_shop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findById(Long id);

    List<Product> findByNameContainingIgnoreCase(String keyword);

    List<Product> findByCategoriesId(Long categoryId);

    List<Product> findByNameContainingIgnoreCaseAndCategoriesId(String keyword, Long categoryId);
}