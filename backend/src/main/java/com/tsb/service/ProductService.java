package com.tsb.service;

import com.tsb.model.Product;
import com.tsb.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> listProducts(String search,
                                      String category,
                                      String size,
                                      String color,
                                      String ageGroup,
                                      Double minPrice,
                                      Double maxPrice,
                                      String sort) {
        Comparator<Product> comparator = switch (sort == null ? "" : sort) {
            case "price-desc" -> Comparator.comparing(Product::getPrice, Comparator.nullsLast(Double::compareTo)).reversed();
            case "name" -> Comparator.comparing(Product::getName, Comparator.nullsLast(String::compareToIgnoreCase));
            default -> Comparator.comparing(Product::getCreatedAt, Comparator.nullsLast(Instant::compareTo)).reversed();
        };

        return productRepository.findAll().stream()
                .filter(product -> product.getActive() == null || product.getActive())
                .filter(product -> contains(product.getName(), search) || contains(product.getDescription(), search) || isBlank(search))
                .filter(product -> matches(product.getCategory(), category))
                .filter(product -> matches(product.getSize(), size))
                .filter(product -> matches(product.getColor(), color))
                .filter(product -> matches(product.getAgeGroup(), ageGroup))
                .filter(product -> minPrice == null || product.getPrice() != null && product.getPrice() >= minPrice)
                .filter(product -> maxPrice == null || product.getPrice() != null && product.getPrice() <= maxPrice)
                .sorted("price-asc".equals(sort)
                        ? Comparator.comparing(Product::getPrice, Comparator.nullsLast(Double::compareTo))
                        : comparator)
                .toList();
    }

    public List<Product> listAllProductsForAdmin() {
        return productRepository.findAll();
    }

    public Product getProduct(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    public Product saveProduct(Product product) {
        if (product.getName() == null || product.getName().isBlank()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (product.getPrice() == null || product.getPrice() < 0) {
            throw new IllegalArgumentException("Product price must be valid");
        }
        if (product.getStock() == null || product.getStock() < 0) {
            throw new IllegalArgumentException("Product stock must be valid");
        }
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    private boolean matches(String value, String filter) {
        return isBlank(filter) || contains(value, filter);
    }

    private boolean contains(String value, String filter) {
        return value != null && !isBlank(filter) && value.toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT));
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
