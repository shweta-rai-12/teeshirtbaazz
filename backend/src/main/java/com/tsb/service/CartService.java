package com.tsb.service;

import com.tsb.model.Cart;
import com.tsb.model.CartItem;
import com.tsb.model.Product;
import com.tsb.model.User;
import com.tsb.repository.CartItemRepository;
import com.tsb.repository.CartRepository;
import com.tsb.repository.ProductRepository;
import com.tsb.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public Cart getCart(String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart cart = new Cart(user);
            return cartRepository.save(cart);
        });
    }

    @Transactional
    public Cart addItem(String userEmail, Long productId, Integer quantity) {
        Cart cart = getCart(userEmail);
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Only " + product.getStock() + " items are in stock");
        }
        CartItem existing = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().orElse(null);
        if (existing != null) {
            if (product.getStock() < existing.getQuantity() + quantity) {
                throw new IllegalArgumentException("Only " + product.getStock() + " items are in stock");
            }
            existing.setQuantity(existing.getQuantity() + quantity);
            cartItemRepository.save(existing);
        } else {
            CartItem item = new CartItem(cart, product, quantity);
            cart.getItems().add(item);
            cartItemRepository.save(item);
        }
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateItem(String userEmail, Long itemId, Integer quantity) {
        Cart cart = getCart(userEmail);
        CartItem item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getId().equals(itemId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        if (item.getProduct().getStock() < quantity) {
            throw new IllegalArgumentException("Only " + item.getProduct().getStock() + " items are in stock");
        }
        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return cart;
    }

    @Transactional
    public Cart removeItem(String userEmail, Long itemId) {
        Cart cart = getCart(userEmail);
        CartItem item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getId().equals(itemId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        return cart;
    }

    @Transactional
    public Cart clearCart(String userEmail) {
        Cart cart = getCart(userEmail);
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        return cartRepository.save(cart);
    }
}
