// src/main/java/com/example/everydayweft/config/SecurityConfig.java
package com.example.everydayweft.config;

import com.example.everydayweft.repository.UserRepository;
import com.example.everydayweft.security.JwtAuthenticationFilter;
import com.example.everydayweft.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public SecurityConfig(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> userRepository.findByEmail(email)
                .map(u -> org.springframework.security.core.userdetails.User.builder()
                        .username(u.getEmail())
                        .password(u.getPassword())
                        .roles(u.getRole().name()) // Misal: "ADMIN" → Spring akan jadi "ROLE_ADMIN"
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder amb = http.getSharedObject(AuthenticationManagerBuilder.class);
        amb.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
        return amb.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtUtil);

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // --- Static resources & public pages ---
                        .requestMatchers("/", "/index", "/index.html").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**", "/favicon.ico").permitAll()

                        // --- H2 Console (only for dev) ---
                        .requestMatchers("/h2-console/**").permitAll()

                        // --- Public API ---
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/products/**").permitAll() // GET products boleh publik

                        // --- Admin HTML page (static) ---
                        .requestMatchers("/admin", "/admin/**").permitAll() // 👈 Izinkan akses ke halaman admin

                        // --- Admin API (dilindungi ketat) ---
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // --- Semua request lain harus autentikasi (tapi tidak perlu role khusus) ---
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.frameOptions(frame -> frame.disable())); // untuk H2 console

        return http.build();
    }
}