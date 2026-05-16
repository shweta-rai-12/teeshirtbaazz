package com.tsb.controller;

import com.tsb.dto.ChatRequest;
import com.tsb.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<String> chat(@RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatService.answer(request.getQuestion()));
    }
}
