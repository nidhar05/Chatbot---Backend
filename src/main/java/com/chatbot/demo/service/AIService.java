package com.chatbot.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;

@Service
public class AIService {

    @Value("${openai.api.key}")
    private String apiKey;

    public String getAIReply(String userMessage) {
        try {
            URL url = new URL("https://api.openai.com/v1/chat/completions");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + apiKey);
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            String body = """
            {
              "model": "gpt-3.5-turbo",
              "messages": [
                {"role": "system", "content": "You are Prakruti AI, an Ayurvedic health assistant. Give natural treatment advice. Ask follow-up questions."},
                {"role": "user", "content": "%s"}
              ]
            }
            """.formatted(userMessage);

            try (OutputStream os = con.getOutputStream()) {
                os.write(body.getBytes());
            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream())
            );

            String line, response = "";
            while ((line = br.readLine()) != null) {
                response += line;
            }

            // simple extraction (for learning purpose)
            return response.contains("content")
                    ? response.split("\"content\":\"")[1].split("\"")[0]
                    : "AI response received.";

        } catch (Exception e) {
            return "Sorry, AI service is temporarily unavailable.";
        }
    }
}
