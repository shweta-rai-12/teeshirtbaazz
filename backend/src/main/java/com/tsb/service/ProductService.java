package com.tsb.service;

import com.tsb.model.Product;
import com.tsb.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

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
                                      String sort,
                                      Boolean inStockOnly) {
        Comparator<Product> comparator = switch (sort == null ? "" : sort) {
            case "price-desc" -> Comparator.comparing(Product::getPrice, Comparator.nullsLast(Double::compareTo)).reversed();
            case "name" -> Comparator.comparing(Product::getName, Comparator.nullsLast(String::compareToIgnoreCase));
            case "stock-desc" -> Comparator.comparing(Product::getStock, Comparator.nullsLast(Integer::compareTo)).reversed();
            default -> Comparator.comparing(Product::getCreatedAt, Comparator.nullsLast(Instant::compareTo)).reversed();
        };

        return productRepository.findAll().stream()
                .filter(product -> product.getActive() == null || product.getActive())
                .filter(product -> matchesSearch(product, search))
                .filter(product -> matches(product.getCategory(), category))
                .filter(product -> matches(product.getSize(), size))
                .filter(product -> matches(product.getColor(), color))
                .filter(product -> matches(product.getAgeGroup(), ageGroup))
                .filter(product -> minPrice == null || product.getPrice() != null && product.getPrice() >= minPrice)
                .filter(product -> maxPrice == null || product.getPrice() != null && product.getPrice() <= maxPrice)
                .filter(product -> !Boolean.TRUE.equals(inStockOnly) || product.getStock() != null && product.getStock() > 0)
                .sorted("price-asc".equals(sort)
                        ? Comparator.comparing(Product::getPrice, Comparator.nullsLast(Double::compareTo))
                        : comparator)
                .toList();
    }

    public Map<String, List<String>> catalogOptions() {
        List<Product> activeProducts = productRepository.findAll().stream()
                .filter(product -> product.getActive() == null || product.getActive())
                .toList();
        Map<String, List<String>> options = new LinkedHashMap<>();
        options.put("categories", distinct(activeProducts.stream().map(Product::getCategory).toList()));
        options.put("sizes", distinct(activeProducts.stream().map(Product::getSize).toList()));
        options.put("colors", distinct(activeProducts.stream().map(Product::getColor).toList()));
        options.put("ageGroups", distinct(activeProducts.stream().map(Product::getAgeGroup).toList()));
        return options;
    }

    public List<Product> listAllProductsForAdmin() {
        return productRepository.findAll();
    }

    public Product getProduct(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    public Product getActiveProduct(Long id) {
        Product product = getProduct(id);
        if (Boolean.FALSE.equals(product.getActive())) {
            throw new IllegalArgumentException("Product not found");
        }
        return product;
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

    private boolean matchesSearch(Product product, String search) {
        return isBlank(search)
                || contains(product.getName(), search)
                || contains(product.getDescription(), search)
                || contains(product.getCategory(), search)
                || contains(product.getAgeGroup(), search)
                || contains(product.getColor(), search)
                || contains(product.getSize(), search);
    }

    private boolean contains(String value, String filter) {
        return value != null && !isBlank(filter) && value.toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT));
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private List<String> distinct(List<String> values) {
        return values.stream()
                .filter(Objects::nonNull)
                .filter(value -> !value.isBlank())
                .distinct()
                .sorted(String::compareToIgnoreCase)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}
