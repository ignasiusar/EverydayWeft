package com.example.everydayweft.service;

import com.example.everydayweft.model.User;
import com.example.everydayweft.model.UserRole;
import com.example.everydayweft.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;  // ✅ Ambil dari Spring
    }

    public User register(String email, String rawPassword) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email sudah digunakan");
        }
        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = new User(email, encodedPassword);
        user.setRole(UserRole.USER);
        return userRepository.save(user);
    }

    public boolean login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Tidak Ditemukan!"));
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User Tidak Ditemukan!"));
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}