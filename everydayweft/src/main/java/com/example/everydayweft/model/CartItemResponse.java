package com.example.everydayweft.model;

public class CartItemResponse {
    private Long id;
    private Long variantId;
    private Long productId;
    private String productName;
    private String size;
    private String color;
    private Double price;
    private int quantity;

    public CartItemResponse() {}

    public CartItemResponse(Long id, Long variantId, Long productId, String productName, String size, String color, Double price, int quantity) {
        this.id = id;
        this.variantId = variantId;
        this.productId = productId;
        this.productName = productName;
        this.size = size;
        this.color = color;
        this.price = price;
        this.quantity = quantity;
    }

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVariantId() { return variantId; }
    public void setVariantId(Long variantId) { this.variantId = variantId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}

//public class CartItemResponse {
//    private Long id;
//    private Long productId;
//    private String productName;
//    private Double price;
//    private int quantity;
//
//    public CartItemResponse(){}
//
//    public CartItemResponse(Long id, Long productId, String productName, Double price, int quantity) {
//        this.id = id;
//        this.productId = productId;
//        this.productName = productName;
//        this.price = price;
//        this.quantity = quantity;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public Long getProductId() {
//        return productId;
//    }
//
//    public void setProductId(Long productId) {
//        this.productId = productId;
//    }
//
//    public String getProductName() {
//        return productName;
//    }
//
//    public void setProductName(String productName) {
//        this.productName = productName;
//    }
//
//    public Double getPrice() {
//        return price;
//    }
//
//    public void setPrice(Double price) {
//        this.price = price;
//    }
//
//    public int getQuantity() {
//        return quantity;
//    }
//
//    public void setQuantity(int quantity) {
//        this.quantity = quantity;
//    }
//}
