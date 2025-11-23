package com.example.everydayweft.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String header = request.getHeader("Authorization");
        System.out.println("=== JWT Filter ===");
        System.out.println("Header: " + header);

        if (header != null && header.startsWith("Bearer ")) {
            final String token = header.substring(7);
            System.out.println("Token: " + token.substring(0, Math.min(20, token.length())) + "...");

            try {
                if (jwtUtil.validateToken(token)) {
                    Claims claims = jwtUtil.getClaims(token);
                    String email = claims.getSubject();
                    String role = claims.get("role", String.class);
                    System.out.println("Token valid. Email: " + email + ", Role: " + role);

                    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                    var auth = new UsernamePasswordAuthenticationToken(email, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    System.out.println("Token TIDAK valid!");
                    SecurityContextHolder.clearContext();
                }
            } catch (Exception e) {
                System.out.println("Error parsing token: " + e.getMessage());
                SecurityContextHolder.clearContext();
            }
        } else {
            System.out.println("No Authorization header");
        }
        filterChain.doFilter(request, response);
    }
}
