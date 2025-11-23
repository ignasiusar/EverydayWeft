package com.example.everydayweft.model;

import java.util.List;
public class CartResponse {
    private long id;
    private Long userId;
    private String username;
    private List<CartItemResponse> items;
    private Double totalPrice;

    public CartResponse(){}

    public CartResponse(long id, Long userId, String username, List<CartItemResponse> items, Double totalPrice) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.items = items;
        this.totalPrice = totalPrice;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public void setItems(List<CartItemResponse> items) {
        this.items = items;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
