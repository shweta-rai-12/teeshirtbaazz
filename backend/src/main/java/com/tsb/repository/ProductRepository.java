package com.tsb.repository;

import com.tsb.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryContainingIgnoreCase(String category);
    List<Product> findByColorContainingIgnoreCase(String color);
    List<Product> findBySizeContainingIgnoreCase(String size);
}
