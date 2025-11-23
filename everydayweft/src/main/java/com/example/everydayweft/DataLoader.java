package com.example.everydayweft;

import com.example.everydayweft.model.Product;
import com.example.everydayweft.model.User;
import com.example.everydayweft.model.UserRole;
import com.example.everydayweft.repository.ProductRepository;
import com.example.everydayweft.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(ProductRepository productRepository, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        return args -> {
            // ==================== BIKIN USER ====================
            // Cek apakah admin sudah ada
            if (!userRepository.findByEmail("admin@example.com").isPresent()) {
                User admin = new User("admin@example.com", passwordEncoder.encode("admin123"));
                admin.setRole(UserRole.ADMIN);
                userRepository.save(admin);
                System.out.println("✅ Admin user created: admin@example.com / admin123");
            }

            // Cek apakah user biasa sudah ada
            if (!userRepository.findByEmail("user@example.com").isPresent()) {
                User user = new User("user@example.com", passwordEncoder.encode("user123"));
                user.setRole(UserRole.USER);
                userRepository.save(user);
                System.out.println("✅ Regular user created: user@example.com / user123");
            }

            // ==================== BIKIN PRODUK ====================
            if (productRepository.count() == 0) { // Cek apakah produk sudah ada
                productRepository.save(new Product("Jaket Kulit Pria", 450000.0, "MEN","https://images.unsplash.com/photo-1551028719-00167b16eac5?ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&q=80&w=1035","Jaket kulit premium untuk pria"));

                productRepository.save(new Product("Kaos Oversize", 180000.0, "MEN","https://dynamic.zacdn.com/393fYE5fqnfEx8iBze77f6GpOGA=/filters:quality(70):format(webp)/https://static-id.zacdn.com/p/moutley-0066-2541605-1.jpg","Kaos oversize nyaman untuk sehari-hari"));

                productRepository.save(new Product("Dress Floral", 250000.0, "WOMEN","https://d2kchovjbwl1tk.cloudfront.net/vendor/444/product/DAN-20241221-SP-X-HOTEL-20254_1736670808511_resized1024-jpg.webp","Dress bunga untuk wanita modern"));

                productRepository.save(new Product("Blouse Kantor", 220000.0, "WOMEN","","Blouse kantor formal"));

                productRepository.save(new Product("Celana Pendek Anak", 90000.0, "KIDS","","Celana pendek nyaman untuk anak"));

                productRepository.save(new Product("Baju Tidur Bayi", 75000.0, "KIDS","","Baju tidur lembut untuk bayi"));

                productRepository.save(new Product("Burbery", 2500000.0, "MEN","https://dynamic.zacdn.com/BiinEwNUlm5eZ1icKOHtyuVI9gA=/filters:quality(70):format(webp)/https://static-id.zacdn.com/p/burberry-1700-7652815-1.jpg","Baju Polo Pria"));

                System.out.println("✅ Produk awal telah ditambahkan!");
            } else {
                System.out.println("⚠️ Produk sudah ada, skip membuat produk baru.");
            }
        };
    }
}