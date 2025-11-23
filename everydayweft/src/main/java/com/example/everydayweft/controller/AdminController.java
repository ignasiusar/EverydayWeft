package com.example.everydayweft.controller;

import com.example.everydayweft.model.Product;
import com.example.everydayweft.model.ProductVariant;
import com.example.everydayweft.model.ProductRequest;
import com.example.everydayweft.model.ProductVariantRequest;
import com.example.everydayweft.model.UpdateStockRequest;
import com.example.everydayweft.repository.ProductVariantRepository;
import com.example.everydayweft.service.ProductService;
import com.example.everydayweft.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ProductService productService;
    private final UserService userService;
    private final ProductVariantRepository variantRepo;

    public AdminController(ProductService productService, UserService userService, ProductVariantRepository variantRepo) {
        this.productService = productService;
        this.userService = userService;
        this.variantRepo = variantRepo;
    }

    // CREATE NEW PRODUCT
    // POST /api/admin/products
    @PostMapping("/products")
    public ResponseEntity<?> addProduct(@RequestBody ProductRequest req) {
        try {
            Product product = new Product(
                    req.getName(),
                    req.getPrice(),
                    req.getCategory(),
                    req.getImageUrl(),
                    req.getDescription()
            );
            Product saved = productService.save(product);
            return ResponseEntity.status(201).body(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // UPDATE PRODUCT
    // PUT /api/admin/products/{id}
    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductRequest req) {
        try {
            Product existing = productService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Produk tidak ditemukan"));

            existing.setName(req.getName());
            existing.setPrice(req.getPrice());
            existing.setCategory(req.getCategory());
            existing.setImageUrl(req.getImageUrl());
            existing.setDescription(req.getDescription());

            Product updated = productService.save(existing);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    // ADD VARIANT
    // POST /api/admin/products/{productId}/variants
    @PostMapping("/products/{productId}/variants")
    public ResponseEntity<?> addProductVariant(
            @PathVariable Long productId,
            @RequestBody ProductVariantRequest req
    ) {
        try {
            Product product = productService.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Produk tidak ditemukan"));

            ProductVariant variant = new ProductVariant();
            variant.setProduct(product);
            variant.setSize(req.getSize());
            variant.setColor(req.getColor());
            variant.setSku(req.getSku());
            variant.setStock(req.getStock());
            variant.setPrice(req.getPrice() != null ? req.getPrice() : product.getPrice());

            ProductVariant saved = variantRepo.save(variant);
            return ResponseEntity.status(201).body(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // UPDATE VARIANT STOCK
    // PUT /api/admin/variants/{variantId}/stock
    @PutMapping("/variants/{variantId}/stock")
    public ResponseEntity<?> updateStock(
            @PathVariable Long variantId,
            @RequestBody UpdateStockRequest req
    ) {
        try {
            ProductVariant variant = variantRepo.findById(variantId)
                    .orElseThrow(() -> new RuntimeException("Varian tidak ditemukan"));

            variant.setStock(req.getStock());
            ProductVariant updated = variantRepo.save(variant);

            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
