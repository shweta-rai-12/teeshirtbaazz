package com.tsb.service;

import com.tsb.model.Product;
import com.tsb.model.User;
import com.tsb.repository.CartItemRepository;
import com.tsb.repository.CartRepository;
import com.tsb.repository.ProductRepository;
import com.tsb.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CartServiceTest {
    @Test
    void rejectsQuantityAboveStock() {
        CartRepository cartRepository = mock(CartRepository.class);
        CartItemRepository cartItemRepository = mock(CartItemRepository.class);
        ProductRepository productRepository = mock(ProductRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        CartService service = new CartService(cartRepository, cartItemRepository, productRepository, userRepository);

        User user = new User("Shopper", "shopper@example.com", "hash", com.tsb.model.Role.ROLE_USER);
        Product product = new Product();
        product.setName("Classic Tee");
        product.setStock(1);
        product.setPrice(499.0);

        when(userRepository.findByEmail("shopper@example.com")).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.empty());
        when(cartRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> service.addItem("shopper@example.com", 10L, 2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("in stock");
    }
}
