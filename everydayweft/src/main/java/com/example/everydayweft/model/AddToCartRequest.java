package com.example.everydayweft.model;

public class AddToCartRequest {
    private Long variantId;
//    private Long productId;
    private int quantity;

    // Getter & Setter

    public Long getVariantId() {
        return variantId;
    }

    public void setVariantId(Long variantId) {
        this.variantId = variantId;
    }

//    public Long getProductId() { return productId; }
//    public void setProductId(Long productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}