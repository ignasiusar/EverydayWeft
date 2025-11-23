package com.example.everydayweft.repository;

import com.example.everydayweft.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public  interface UserRepository extends JpaRepository<User,Long>{

        Optional<User> findByEmail(String email);  // ← Ganti dari findByUsername

        // Optional: Kalau kamu tetap pengen bisa cari by username (untuk backward compatibility)
        // Optional<User> findByUsername(String username);
//    Optional<User> findByUsername(String username);
}