package com.chatbot.demo.service;

import org.springframework.stereotype.Service;

@Service
public class ChatService {

    public String getBotReply(String userMessage) {

        userMessage = userMessage.toLowerCase();

        if(userMessage.contains("hi") || userMessage.contains("hello"))
            return "Hello! I am Prakruti AI 🤖";

        if(userMessage.contains("your name"))
            return "My name is Prakruti AI, your assistant!";

        if(userMessage.contains("how are you"))
            return "I'm doing great! Thanks for asking 😊";

        return "Sorry, I didn’t understand that yet.";
    }
}
