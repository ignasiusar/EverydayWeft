package com.example.everydayweft.repository;

import com.example.everydayweft.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findByProductId(Long productId);
    ProductVariant findByProductIdAndSizeAndColor(Long productId, String size, String color);
}
