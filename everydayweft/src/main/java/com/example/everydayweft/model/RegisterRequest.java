package com.example.everydayweft.model;

import jakarta.validation.constraints.Email;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class RegisterRequest {
    @Email(message = "Format email tidak valid")
    @NotBlank(message = "Email harus diisi")
    private String email;

    @Size(min = 6, message = "Password minimal 6 karakter")
    @NotBlank(message = "Password harus diisi")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
