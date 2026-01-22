package com.chatbot.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
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

    // Injected from application.properties (local)
    // OR Render Environment Variable: GROQ_API_KEY
    @Value("${groq.api.key:}")
    private String apiKey;

    // Optional startup log (remove after testing)
    @PostConstruct
    public void logEnvCheck() {
        System.out.println("Groq API key present: " + (apiKey != null && !apiKey.isBlank()));
    }

    public String getAIReply(String userMessage) {

        // Safety check
        if (apiKey == null || apiKey.isBlank()) {
            return "⚠️ AI service is not configured right now.";
        }

        try {
            URL url = new URL("https://api.groq.com/openai/v1/chat/completions");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + apiKey);
            con.setRequestProperty("Content-Type", "application/json");
            con.setConnectTimeout(15000);
            con.setReadTimeout(15000);
            con.setDoOutput(true);

            // 🧠 PURE CONVERSATIONAL PROMPT
            String body = """
            {
              "model": "llama-3.1-8b-instant",
              "messages": [
                {
                  "role": "system",
                  "content": "You are Prakruti AI, a friendly and caring Ayurvedic health assistant. Speak naturally like a human. If the user greets, greet back. If they say they are not well, respond with empathy and ask what they are experiencing. If symptoms are described, explain possible causes and remedies. Introduce Prakruti concepts (Vata, Pitta, Kapha) ONLY if it naturally fits the conversation. Do NOT force Prakruti assessment. Ask only ONE follow-up question at a time."
                },
                {
                  "role": "user",
                  "content": "%s"
                }
              ],
              "max_tokens": 300,
              "temperature": 0.7
            }
            """.formatted(userMessage);

            try (OutputStream os = con.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            BufferedReader br;
            if (con.getResponseCode() >= 400) {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            }

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            String res = response.toString();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(res);

            // 🔍 SAFE extraction
            JsonNode choicesNode = root.path("choices");
            String aiReply = null;

            if (choicesNode.isArray() && choicesNode.size() > 0) {
                aiReply = choicesNode
                        .get(0)
                        .path("message")
                        .path("content")
                        .asText();
            }

            // 🛡️ Handle Groq error response
            if (aiReply == null || aiReply.isBlank()) {

                if (root.has("error")) {
                    System.out.println("Groq error response: " + root.get("error"));
                    return "⚠️ I’m having a little trouble responding right now. Please try again.";
                }

                // Friendly conversational fallback
                return "I’m here to help 🙂 Could you tell me a bit more?";
            }

            return aiReply;

        } catch (Exception e) {
            e.printStackTrace();
            return "⚠️ Prakruti AI is temporarily unavailable. Please try again.";
        }
    }
}
