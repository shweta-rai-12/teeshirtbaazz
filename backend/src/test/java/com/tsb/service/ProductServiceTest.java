package com.tsb.service;

import com.tsb.model.Product;
import com.tsb.repository.ProductRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductServiceTest {
    @Test
    void filtersProductsByTshirtAttributes() {
        ProductRepository repository = mock(ProductRepository.class);
        ProductService service = new ProductService(repository);
        when(repository.findAll()).thenReturn(List.of(
                product("Classic Tee", "Men", "Adult", "Black", "M", 499.0, true),
                product("Kids Tee", "Kids", "Kids", "Green", "S", 349.0, true),
                product("Hidden Tee", "Women", "Adult", "Pink", "L", 699.0, false)
        ));

        List<Product> result = service.listProducts("classic", "Men", "M", "Black", "Adult", 100.0, 600.0, "price-asc");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Classic Tee");
    }

    private Product product(String name, String category, String ageGroup, String color, String size, Double price, Boolean active) {
        Product product = new Product();
        product.setName(name);
        product.setCategory(category);
        product.setAgeGroup(ageGroup);
        product.setColor(color);
        product.setSize(size);
        product.setPrice(price);
        product.setStock(10);
        product.setDescription(name + " description");
        product.setActive(active);
        return product;
    }
}
