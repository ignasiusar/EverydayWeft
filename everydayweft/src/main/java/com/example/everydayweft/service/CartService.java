package com.example.everydayweft.service;

import com.example.everydayweft.model.*;
import com.example.everydayweft.repository.CartItemRepository;
import com.example.everydayweft.repository.CartRepository;
import com.example.everydayweft.repository.ProductVariantRepository;
import com.example.everydayweft.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
                       ProductVariantRepository productVariantRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productVariantRepository = productVariantRepository;
        this.userRepository = userRepository;
    }

    public Cart getOrCreateCart(User user) {
        System.out.println("=== DEBUG getOrCreateCart ===");
        System.out.println("Input user ID: " + user.getId());

        Cart existingCart = cartRepository.findByUser(user).orElse(null);
        System.out.println("Existing cart found: " + existingCart);

        if (existingCart != null) {
            System.out.println("Returning existing cart ID: " + existingCart.getId());
            return existingCart;
        } else {
            System.out.println("Creating new cart...");
            Cart newCart = new Cart(user);
            System.out.println("About to save cart...");
            try {
                Cart savedCart = cartRepository.save(newCart);
                System.out.println("Cart saved successfully with ID: " + savedCart.getId());
                return savedCart;
            } catch (Exception e) {
                System.out.println("ERROR saving cart: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
        }
    }

    public void addToCart(Long userId, Long variantId, int quantity) {
        System.out.println("=== DEBUG addToCart ===");
        System.out.println("Input userId: " + userId);
        System.out.println("Input variantId: " + variantId);
        System.out.println("Input quantity: " + quantity);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        System.out.println("Found user ID: " + user.getId());

        Cart cart = getOrCreateCart(user);
        System.out.println("Got cart ID: " + cart.getId());
        System.out.println("Cart user ID: " + cart.getUser().getId()); // Tambahkan ini

        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Varian produk tidak ditemukan"));
        System.out.println("Found variant ID: " + variant.getId());
        System.out.println("Variant product ID: " + variant.getProduct().getId()); // Tambahkan ini
        System.out.println("Variant stock: " + variant.getStock()); // Tambahkan ini

        if (variant.getStock() < quantity) {
            throw new RuntimeException("Stok tidak mencukupi. Tersedia: " + variant.getStock());
        }

        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getVariant().getId().equals(variantId))
                .findFirst()
                .orElse(null);
        System.out.println("Existing item found: " + existingItem);

        if (existingItem != null) {
            System.out.println("Updating existing item...");
            int totalQuantity = existingItem.getQuantity() + quantity;
            if (variant.getStock() < totalQuantity) {
                throw new RuntimeException("Stok tidak mencukupi. Tersedia: " + variant.getStock() + ", Total yang diminta: " + totalQuantity);
            }
            existingItem.setQuantity(totalQuantity);
            System.out.println("About to save existing item...");
            System.out.println("Existing item cart ID: " + existingItem.getCart().getId()); // Tambahkan ini
            System.out.println("Existing item variant ID: " + existingItem.getVariant().getId()); // Tambahkan ini
            try {
                CartItem savedItem = cartItemRepository.save(existingItem);
                System.out.println("Existing item saved with ID: " + savedItem.getId());
            } catch (Exception e) {
                System.out.println("ERROR saving existing item: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
        } else {
            System.out.println("Creating new CartItem...");
            System.out.println("Cart ID for new item: " + cart.getId()); // Pastikan cart punya id
            System.out.println("Variant ID for new item: " + variant.getId()); // Pastikan variant punya id
            System.out.println("Variant price: " + variant.getPrice()); // Pastikan price tidak null

            CartItem newItem = new CartItem(cart, variant, quantity, variant.getPrice());
            System.out.println("New CartItem created:");
            System.out.println("- Cart reference ID: " + (newItem.getCart() != null ? newItem.getCart().getId() : "NULL"));
            System.out.println("- Variant reference ID: " + (newItem.getVariant() != null ? newItem.getVariant().getId() : "NULL"));
            System.out.println("- Quantity: " + newItem.getQuantity());
            System.out.println("- Price: " + newItem.getPrice());

            cart.getItems().add(newItem);
            System.out.println("About to save new CartItem...");
            try {
                CartItem savedItem = cartItemRepository.save(newItem); // Error bisa muncul di sini
                System.out.println("New CartItem saved successfully with ID: " + savedItem.getId());
            } catch (Exception e) {
                System.out.println("ERROR saving new CartItem: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
        }
    }

    public Cart getCartByUser(User user) {
        System.out.println("=== DEBUG getCartByUser ===");
        System.out.println("Input user ID: " + user.getId());
        return cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Keranjang tidak ditemukan"));
    }

    public void removeItemById(Long itemId) {
        System.out.println("=== DEBUG removeItemById ===");
        System.out.println("Item ID to remove: " + itemId);
        cartItemRepository.deleteById(itemId);
        System.out.println("Item removed successfully");
    }

    public void updateItemQuantity(Long itemId, int quantity) {
        System.out.println("=== DEBUG updateItemQuantity ===");
        System.out.println("Item ID: " + itemId);
        System.out.println("New quantity: " + quantity);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item keranjang tidak ditemukan"));
        System.out.println("Found item ID: " + item.getId());

        if (quantity > item.getVariant().getStock()) {
            throw new RuntimeException("Stok tidak mencukupi. Tersedia: " + item.getVariant().getStock());
        }

        if (quantity <= 0) {
            System.out.println("Deleting item because quantity <= 0");
            cartItemRepository.delete(item);
        } else {
            System.out.println("Updating item quantity...");
            item.setQuantity(quantity);
            System.out.println("About to save updated item...");
            try {
                CartItem savedItem = cartItemRepository.save(item);
                System.out.println("Updated item saved with ID: " + savedItem.getId());
            } catch (Exception e) {
                System.out.println("ERROR saving updated item: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
        }
    }

    public CartResponse getCartResponseByUser(User user) {
        System.out.println("=== DEBUG getCartResponseByUser ===");
        System.out.println("Input user ID: " + user.getId());

        Cart cart = getCartByUser(user);
        System.out.println("Cart items count: " + cart.getItems().size());

        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(item -> {
                    System.out.println("Mapping item ID: " + item.getId());
                    return new CartItemResponse(
                            item.getId(),
                            item.getVariant().getId(),
                            item.getVariant().getProduct().getId(),
                            item.getVariant().getProduct().getName(),
                            item.getVariant().getSize(),
                            item.getVariant().getColor(),
                            item.getPrice(),
                            item.getQuantity());
                })
                .toList();

        return new CartResponse(
                cart.getId(),
                user.getId(),
                user.getEmail(),
                itemResponses,
                cart.getTotalPrice());
    }
}