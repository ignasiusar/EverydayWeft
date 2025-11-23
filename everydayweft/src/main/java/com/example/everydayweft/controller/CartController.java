package com.example.everydayweft.controller;

import com.example.everydayweft.model.Cart;
import com.example.everydayweft.model.User;
import com.example.everydayweft.model.AddToCartRequest;
import com.example.everydayweft.model.UpdateQuantityRequest;
import com.example.everydayweft.service.CartService;
import com.example.everydayweft.service.UserService;
import com.example.everydayweft.model.CartResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class CartController {
    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @PostMapping("/cart/add")
    public ResponseEntity<?> addToCart(@RequestBody AddToCartRequest request) {
        System.out.println("=== DEBUG CartController addToCart ===");
        System.out.println("Request variantId: " + request.getVariantId());
        System.out.println("Request quantity: " + request.getQuantity());

        // ✅ Ambil user dari SecurityContext (bukan session!)
        Long userId = getCurrentUserId();
        System.out.println("Authenticated user ID: " + userId);

        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Harus login dulu"));
        }

        try {
            System.out.println("About to call cartService.addToCart...");
            cartService.addToCart(userId, request.getVariantId(), request.getQuantity());

            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
            Cart cart = cartService.getCartByUser(user);

            return ResponseEntity.ok(Map.of(
                    "message", "Produk berhasil ditambahkan ke keranjang",
                    "totalItems", cart.getItems().size(),
                    "totalPrice", cart.getTotalPrice()
            ));
        } catch (Exception e) {
            System.out.println("ERROR in CartController: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/cart/user/{userId}")
    public ResponseEntity<?> getCart(@PathVariable Long userId) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null || !currentUserId.equals(userId)) {
            return ResponseEntity.status(403).body(Map.of("error", "Akses ditolak"));
        }

        try {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
            CartResponse cartResponse = cartService.getCartResponseByUser(user);
            return ResponseEntity.ok(cartResponse);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/cart/item/{itemId}")
    public ResponseEntity<?> removeCartItem(@PathVariable Long itemId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Harus login dulu"));
        }

        try {
            cartService.removeItemById(itemId);
            return ResponseEntity.ok(Map.of("message", "Item berhasil dihapus dari keranjang"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/cart/item/{itemId}/quantity")
    public ResponseEntity<?> updateItemQuantity(@PathVariable Long itemId, @RequestBody UpdateQuantityRequest request) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Harus login dulu"));
        }

        try {
            cartService.updateItemQuantity(itemId, request.getQuantity());
            return ResponseEntity.ok(Map.of("message", "Jumlah item berhasil diperbarui"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ Helper: Ambil userId dari SecurityContext
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        String email;

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            email = (String) principal; // ← Ini yang terjadi di JWT-mu
        } else {
            return null;
        }

        User user = userService.findByEmail(email);
        return user != null ? user.getId() : null;
    }
}