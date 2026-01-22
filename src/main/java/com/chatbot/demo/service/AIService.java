package com.chatbot.demo.service;

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

    // 🔑 Injected from Render Environment Variable: GROQ_API_KEY
    @Value("${groq.api.key:}")
    private String apiKey;

    @PostConstruct
    public void logEnvCheck() {
        System.out.println(
                "Groq API key present: " +
                        (apiKey != null && !apiKey.isBlank())
        );
    }

    public String getAIReply(String userMessage) {

        // 🛡️ Safety: if key is missing, do NOT crash app
        if (apiKey == null || apiKey.isBlank()) {
            return "⚠️ AI service is not configured properly. Please try again later.";
        }

        try {
            // 🔹 Groq API endpoint
            URL url = new URL("https://api.groq.com/openai/v1/chat/completions");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + apiKey);
            con.setRequestProperty("Content-Type", "application/json");
            con.setConnectTimeout(15000); // 15 seconds
            con.setReadTimeout(15000);
            con.setDoOutput(true);

            // 🔹 Request body (chat-style, not completion-style)
            String body = """
            {
              "model": "llama3-8b-8192",
              "messages": [
                {
                  "role": "system",
                  "content": "You are Prakruti AI, an Ayurvedic health assistant. Respond clearly and safely. Do NOT invent user replies. Ask at most ONE follow-up question."
                },
                {
                  "role": "user",
                  "content": "%s"
                }
              ]
            }
            """.formatted(userMessage);

            // 🔹 Send request
            try (OutputStream os = con.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            // 🔹 Read response
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8)
            );

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            String res = response.toString();

            // 🔹 Extract AI message (simple but effective)
            String aiReply = res.split("\"content\":\"")[1].split("\"")[0];

            // 🔹 Cleanup formatting
            aiReply = aiReply.replace("\\n", "\n");

            // 🔹 Extra safety: remove any fake user text
            if (aiReply.contains("User:")) {
                aiReply = aiReply.split("User:")[0];
            }
            if (aiReply.contains("User message:")) {
                aiReply = aiReply.split("User message:")[0];
            }

            return aiReply;

        } catch (Exception e) {
            e.printStackTrace();
            return "⚠️ Prakruti AI is temporarily unavailable. Please try again.";
        }
    }
}
