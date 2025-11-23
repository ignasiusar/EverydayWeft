package com.example.everydayweft.model;

import jakarta.persistence.*;
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double price;
    private String category;
    private String imageUrl;
private String description;

    public Product(){}

    public Product(Long id, String name, Double price, String category, String imageUrl, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.description = description;
    }
    public Product(String name, Double price, String category, String imageUrl, String description) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.description = description;
    }
    public Long getId(){return id;}
    public void setId(Long id){this.id = id;}
     
    public String getName(){return name;}
    public void setName(String name){this.name = name;}
     
    public Double getPrice(){return price;}
    public void setPrice(Double price){this.price = price;}

     public String getCategory(){return category;}
    public void setCategory(String category){this.category = category;}

    public String getImageUrl(){return imageUrl;}
    public void setImageUrl(String imageUrl){this.imageUrl = imageUrl;}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
