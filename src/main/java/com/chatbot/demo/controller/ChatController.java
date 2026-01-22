package com.chatbot.demo.controller;

import com.chatbot.demo.model.ChatRequest;
import com.chatbot.demo.model.ChatResponse;
import com.chatbot.demo.service.AIService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin("*")
public class ChatController {

    private final AIService aiService;

    public ChatController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String reply = aiService.getAIReply(request.getMessage());
        return new ChatResponse(reply);
    }
}
