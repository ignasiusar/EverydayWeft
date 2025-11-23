package com.example.everydayweft.controller;

import com.example.everydayweft.model.Product;
import com.example.everydayweft.model.ProductVariant;
import com.example.everydayweft.repository.ProductVariantRepository;
import com.example.everydayweft.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final ProductVariantRepository productVariantRepository;

    public ProductController(ProductService productService, ProductVariantRepository productVariantRepository) {
        this.productService = productService;
        this.productVariantRepository = productVariantRepository;
    }

    // ✅ ENDPOINT PAGINATION BARU (harus di awal karena paling spesifik)
    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        // Batasi maksimal size agar tidak overload
        size = Math.min(size, 50);

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.findAll(pageable);
        return ResponseEntity.ok(products);
    }

    // ❌ HAPUS METHOD INI karena bentrok dengan pagination
    // public ResponseEntity<List<Product>> getAllProducts() { ... }

    // GET /api/products/category/{categoryName}
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String categoryName) {
        List<Product> list = productService.getProductsByCategory(categoryName);
        return ResponseEntity.ok(list);
    }

    // GET /api/products/{productId}
    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable Long productId) {
        return productService.findById(productId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body((Product) Map.of("error", "Produk tidak ditemukan")));
    }

    // GET /api/products/{productId}/variants
    @GetMapping("/{productId}/variants")
    public ResponseEntity<?> getProductVariants(@PathVariable Long productId) {
        List<ProductVariant> variants = productVariantRepository.findByProductId(productId);
        return ResponseEntity.ok(variants);
    }

    // GET /api/products/{productId}/variant?color=...&size=...
    @GetMapping("/{productId}/variant")
    public ResponseEntity<?> getProductVariant(
            @PathVariable Long productId,
            @RequestParam String color,
            @RequestParam String size) {

        ProductVariant variant = productVariantRepository.findByProductIdAndSizeAndColor(productId, size, color);
        if (variant == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Varian tidak ditemukan"));
        }
        return ResponseEntity.ok(variant);
    }
}