package com.tsb.service;

import com.tsb.model.FaqItem;
import com.tsb.repository.FaqItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Locale;

@Service
public class ChatService {
    private final FaqItemRepository faqItemRepository;

    private final Map<String, String> faqAnswers = Map.of(
            "shipping", "Orders are shipped within 2-3 business days.",
            "return", "You can request a return within 7 days of delivery.",
            "payment", "We support UPI, card, and cash on delivery payment methods.",
            "custom", "Custom orders are reviewed by our team, and you will receive a confirmation soon.",
            "size", "Check product sizes from S to XXL. If unsure, choose your regular t-shirt size.",
            "order", "You can track your order from the Orders page after checkout."
    );

    public ChatService(FaqItemRepository faqItemRepository) {
        this.faqItemRepository = faqItemRepository;
    }

    public String answer(String question) {
        String normalized = question == null ? "" : question.toLowerCase();
        List<FaqItem> faqItems = faqItemRepository.findByActiveTrue();
        return faqItems.stream()
                .filter(item -> matches(normalized, item))
                .map(FaqItem::getAnswer)
                .findFirst()
                .orElseGet(() -> fallbackAnswer(normalized));
    }

    private boolean matches(String normalizedQuestion, FaqItem item) {
        String haystack = (item.getCategory() + " " + item.getQuestion()).toLowerCase(Locale.ROOT);
        return List.of(normalizedQuestion.split("\\W+")).stream()
                .filter(token -> token.length() > 2)
                .anyMatch(haystack::contains);
    }

    private String fallbackAnswer(String normalized) {
        return faqAnswers.entrySet().stream()
                .filter(entry -> normalized.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse("Please contact support for help with your question.");
    }
}
