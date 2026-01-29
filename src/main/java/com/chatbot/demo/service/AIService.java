package com.chatbot.demo.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class AIService {

    public String getAIReply(String userMessage) {

        try {
            // 🔹 Local Ollama URL
            URL url = new URL("http://localhost:11434/api/generate");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            // 🔹 Use LOW-RAM model
            String body = """
            {
              "model": "qwen2.5:1.5b",
              "prompt": "You are Prakruti AI, an Ayurvedic health assistant.
                    
                                 Rules:
                                 1. The user may answer in full sentences or paragraphs.
                                 2. Ask ONLY ONE follow-up health question at a time.
                                 3. Focus on symptoms, duration, sensation, and triggers.
                                 4. Give a final accurate diagnosis with valuable reasons.
                                 5. Keep responses short and clear.
                    
                                 Task:
                                 - Understand the user's symptoms.
                                 - Ask the next relevant question needed for Prakruti assessment.
                    
                                 User input:
                                 <USER_MESSAGE>",
              "stream": false
            }
            """.formatted(userMessage);

            try (OutputStream os = con.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8)
            );

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            String res = response.toString();

            // 🔹 Extract AI reply text
            String aiReply;
            if (res.contains("\"response\"")) {
                aiReply = res.split("\"response\":\"")[1].split("\"")[0];
            } else {
                aiReply = "I need a little more information to understand your condition.";
            }

            // 🔹 Prakruti assessment from YOUR logic
            String prakruti = PrakrutiAnalyzer.assess(userMessage);

            // 🔹 Final combined response
            return aiReply + "\n\n🧘 Prakruti Assessment: " + prakruti;

        } catch (Exception e) {
            e.printStackTrace();
            return "Local AI is not running. Please make sure Ollama is running.";
        }
    }
}