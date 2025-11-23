package com.example.everydayweft.controller;

import com.example.everydayweft.model.LoginRequest;
import com.example.everydayweft.model.RegisterRequest;
import com.example.everydayweft.model.User;
import com.example.everydayweft.repository.UserRepository;
import com.example.everydayweft.security.JwtUtil;
import com.example.everydayweft.service.RefreshTokenService;
import com.example.everydayweft.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserService userService;
    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authManager,
                          UserService userService,
                          UserRepository userRepo,
                          JwtUtil jwtUtil,
                          RefreshTokenService refreshTokenService) {
        this.authManager = authManager;
        this.userService = userService;
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    // ✅ BARU: Endpoint untuk verifikasi user saat ini
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token = header.substring(7);
        try {
            if (jwtUtil.validateToken(token)) {
                Claims claims = jwtUtil.getClaims(token);
                String email = claims.getSubject();
                String role = claims.get("role", String.class);

                return ResponseEntity.ok(Map.of(
                        "email", email,
                        "role", role
                ));
            } else {
                return ResponseEntity.status(401).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            User user = userService.register(req.getEmail(), req.getPassword());
            return ResponseEntity.ok(Map.of("userId", user.getId(), "email", user.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpServletResponse response) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );

            User user = userService.findByEmail(req.getEmail());

            // claims — include role for convenience
            Map<String, Object> claims = Map.of("role", user.getRole().name());

            String accessToken = jwtUtil.generateAccessToken(user.getEmail(), claims);

            var refreshToken = refreshTokenService.createTokenForUser(user.getId());

            // Set httpOnly cookie with refresh token
            ResponseCookie cookie = ResponseCookie.from("refresh-token", refreshToken.getToken())
                    .httpOnly(true)
                    .secure(false) // set true in prod (HTTPS)
                    .path("/api/auth/refresh")
                    .maxAge(60L * 60 * 24 * 30) // 30 days
                    .sameSite("Lax")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity.ok(Map.of(
                    "accessToken", accessToken,
                    "userId", user.getId(),
                    "email", user.getEmail(),
                    "role", user.getRole().name()
            ));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentication failed"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        // cookie read
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return ResponseEntity.status(401).body(Map.of("error", "No refresh token"));

        String token = null;
        for (Cookie c : cookies) {
            if ("refresh-token".equals(c.getName())) {
                token = c.getValue();
                break;
            }
        }
        if (token == null) return ResponseEntity.status(401).body(Map.of("error", "No refresh token"));

        var opt = refreshTokenService.findByToken(token);
        if (opt.isEmpty()) return ResponseEntity.status(401).body(Map.of("error", "Invalid refresh token"));

        var rt = opt.get();
        if (rt.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenService.delete(rt);
            return ResponseEntity.status(401).body(Map.of("error", "Refresh token expired"));
        }

        // load user and issue new access token (optionally rotate refresh token)
        var user = userRepo.findById(rt.getUserId()).orElseThrow();
        Map<String, Object> claims = Map.of("role", user.getRole().name());
        String newAccess = jwtUtil.generateAccessToken(user.getEmail(), claims);

        // optional: rotate refresh token (create new, delete old)
        refreshTokenService.delete(rt);
        var newRt = refreshTokenService.createTokenForUser(user.getId());
        ResponseCookie cookie = ResponseCookie.from("refresh-token", newRt.getToken())
                .httpOnly(true)
                .secure(false) // true in prod
                .path("/api/auth/refresh")
                .maxAge(60L * 60 * 24 * 30)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(Map.of("accessToken", newAccess));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        // delete refresh cookie and remove from DB if possible
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("refresh-token".equals(c.getName())) {
                    var opt = refreshTokenService.findByToken(c.getValue());
                    opt.ifPresent(refreshTokenService::delete);
                }
            }
        }
        // clear cookie
        ResponseCookie cookie = ResponseCookie.from("refresh-token", "")
                .httpOnly(true)
                .secure(false)
                .path("/api/auth/refresh")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(Map.of("logout", true));
    }
}