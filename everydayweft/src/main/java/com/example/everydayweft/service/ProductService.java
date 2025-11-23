package com.example.everydayweft.service;

import com.example.everydayweft.model.Product;
import com.example.everydayweft.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // ✅ Method pagination baru
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    // ❌ HAPUS method ini karena sudah ada findById
    // public List<Product> getAllProducts() {
    //     return productRepository.findAll();
    // }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryIgnoreCase(category);
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }
}