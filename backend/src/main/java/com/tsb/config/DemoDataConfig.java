package com.tsb.config;

import com.tsb.model.FaqItem;
import com.tsb.model.Product;
import com.tsb.model.Role;
import com.tsb.model.User;
import com.tsb.repository.FaqItemRepository;
import com.tsb.repository.ProductRepository;
import com.tsb.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DemoDataConfig {
    @Bean
    CommandLineRunner seedDemoData(ProductRepository productRepository,
                                   FaqItemRepository faqItemRepository,
                                   UserRepository userRepository,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("admin@teeshirtbazz.com").isEmpty()) {
                userRepository.save(new User("TeeShirtBazz Admin", "admin@teeshirtbazz.com", passwordEncoder.encode("Admin@123"), Role.ROLE_ADMIN));
            }

            if (productRepository.count() == 0) {
                productRepository.save(product("Classic Cotton Tee", "Men", "Adult", "Black", "M", 499.0, 30,
                        "Everyday 180 GSM cotton t-shirt with a clean regular fit.",
                        "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=800&q=80"));
                productRepository.save(product("Oversized Graphic Tee", "Women", "Adult", "Lavender", "L", 799.0, 18,
                        "Soft oversized t-shirt for casual styling and custom layering.",
                        "https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?auto=format&fit=crop&w=800&q=80"));
                productRepository.save(product("Kids Dino Print Tee", "Kids", "Kids", "Green", "S", 349.0, 25,
                        "Play-ready kids t-shirt with a durable print and breathable fabric.",
                        "https://images.unsplash.com/photo-1519238263530-99bdd11df2ea?auto=format&fit=crop&w=800&q=80"));
                productRepository.save(product("Premium Polo T-Shirt", "Men", "Adult", "Navy", "XL", 999.0, 12,
                        "Collared t-shirt with premium pique texture for smarter casual wear.",
                        "https://images.unsplash.com/photo-1618354691373-d851c5c3a990?auto=format&fit=crop&w=800&q=80"));
            }

            if (faqItemRepository.count() == 0) {
                faqItemRepository.save(faq("Shipping", "When will my order ship?", "Orders are usually shipped within 2-3 business days."));
                faqItemRepository.save(faq("Returns", "Can I return a t-shirt?", "Return requests can be raised from the Orders page after your order is confirmed."));
                faqItemRepository.save(faq("Size", "How do I choose a size?", "Use your regular t-shirt size. Product cards show available size and stock."));
                faqItemRepository.save(faq("Custom", "How do custom t-shirt requests work?", "Submit logo URL, text, size, and color. Admin reviews the request before converting it to an order."));
            }
        };
    }

    private Product product(String name, String category, String ageGroup, String color, String size, Double price, Integer stock, String description, String imageUrl) {
        Product product = new Product();
        product.setName(name);
        product.setCategory(category);
        product.setAgeGroup(ageGroup);
        product.setColor(color);
        product.setSize(size);
        product.setPrice(price);
        product.setStock(stock);
        product.setDescription(description);
        product.setImageUrl(imageUrl);
        product.setActive(true);
        return product;
    }

    private FaqItem faq(String category, String question, String answer) {
        FaqItem item = new FaqItem();
        item.setCategory(category);
        item.setQuestion(question);
        item.setAnswer(answer);
        item.setActive(true);
        return item;
    }
}
