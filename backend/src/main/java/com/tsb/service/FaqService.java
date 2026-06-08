package com.tsb.service;

import com.tsb.dto.FaqItemRequest;
import com.tsb.model.FaqItem;
import com.tsb.repository.FaqItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FaqService {
    private final FaqItemRepository faqItemRepository;

    public FaqService(FaqItemRepository faqItemRepository) {
        this.faqItemRepository = faqItemRepository;
    }

    public List<FaqItem> list() {
        return faqItemRepository.findAll();
    }

    public FaqItem create(FaqItemRequest request) {
        FaqItem item = new FaqItem();
        apply(item, request);
        return faqItemRepository.save(item);
    }

    public FaqItem update(Long id, FaqItemRequest request) {
        FaqItem item = faqItemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("FAQ not found"));
        apply(item, request);
        return faqItemRepository.save(item);
    }

    public void delete(Long id) {
        faqItemRepository.deleteById(id);
    }

    private void apply(FaqItem item, FaqItemRequest request) {
        if (request.getQuestion() == null || request.getQuestion().isBlank()) {
            throw new IllegalArgumentException("FAQ question is required");
        }
        if (request.getAnswer() == null || request.getAnswer().isBlank()) {
            throw new IllegalArgumentException("FAQ answer is required");
        }
        item.setCategory(request.getCategory());
        item.setQuestion(request.getQuestion());
        item.setAnswer(request.getAnswer());
        item.setActive(request.getActive() == null || request.getActive());
    }
}
