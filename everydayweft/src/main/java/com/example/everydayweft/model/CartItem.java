package com.example.everydayweft.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    private int quantity;
    private Double price;

    // Constructor tanpa id (untuk buat baru)
    public CartItem() {}

    public CartItem(Cart cart, ProductVariant variant, int quantity, Double price) {
        this.cart = cart;
        this.variant = variant;
        this.quantity = quantity;
        this.price = price;
    }

    // Constructor dengan id (untuk dari database)
    public CartItem(Long id, Cart cart, ProductVariant variant, int quantity, Double price) {
        this.id = id;
        this.cart = cart;
        this.variant = variant;
        this.quantity = quantity;
        this.price = price;
    }

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }

    public ProductVariant getVariant() { return variant; }
    public void setVariant(ProductVariant variant) { this.variant = variant; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }  // ✅ Fix ini

    // Getter tambahan untuk frontend
    public String getProductName() { return variant.getProduct().getName(); }
    public String getSize() { return variant.getSize(); }
    public String getColor() { return variant.getColor(); }
    public Long getProductId() { return variant.getProduct().getId(); }
}