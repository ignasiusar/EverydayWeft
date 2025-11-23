package com.example.everydayweft.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;  // Ganti dari username

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)  // Tambahkan ini
    private UserRole role = UserRole.USER;  // Default: USER

    // Konstruktor, getter & setter
    public User() {}

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // getter & setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}