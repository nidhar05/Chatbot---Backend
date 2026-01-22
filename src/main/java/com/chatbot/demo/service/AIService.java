package com.chatbot.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class AIService {

    @Value("${groq.api.key}")
    private String apiKey;

    public String getAIReply(String userMessage) {
        try {
            URL url = new URL("https://api.groq.com/openai/v1/chat/completions");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + apiKey);
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            String body = """
            {
              "model": "llama3-8b-8192",
              "messages": [
                {"role": "system", "content": "You are Prakruti AI, an Ayurvedic assistant. Give helpful, safe advice and ask ONE question only."},
                {"role": "user", "content": "%s"}
              ]
            }
            """.formatted(userMessage);

            con.getOutputStream().write(body.getBytes());

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String response = br.lines().reduce("", String::concat);

            return response.split("\"content\":\"")[1].split("\"")[0];

        } catch (Exception e) {
            return "AI service is temporarily unavailable.";
        }
    }
}
