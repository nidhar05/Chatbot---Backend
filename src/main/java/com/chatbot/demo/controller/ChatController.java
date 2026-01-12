package com.chatbot.demo.controller;

import com.chatbot.demo.model.ChatRequest;
import com.chatbot.demo.model.ChatResponse;
import com.chatbot.demo.service.ChatService;

import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String reply = chatService.getBotReply(request.getMessage());
        return new ChatResponse(reply);
    }
}
