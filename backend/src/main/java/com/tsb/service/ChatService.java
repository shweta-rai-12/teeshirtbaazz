package com.tsb.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ChatService {
    private final Map<String, String> faqAnswers = Map.of(
            "shipping", "Orders are shipped within 2-3 business days.",
            "return", "You can request a return within 7 days of delivery.",
            "payment", "We support UPI, card, and cash on delivery payment methods.",
            "custom", "Custom orders are reviewed by our team, and you will receive a confirmation soon."
    );

    public String answer(String question) {
        String normalized = question == null ? "" : question.toLowerCase();
        return faqAnswers.entrySet().stream()
                .filter(entry -> normalized.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse("Please contact support for help with your question.");
    }
}
