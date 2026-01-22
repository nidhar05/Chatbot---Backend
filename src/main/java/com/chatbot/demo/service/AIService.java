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
              "model": "tinyllama",
              "prompt": "You are Prakruti AI, an Ayurvedic health assistant. The user may respond in full sentences or paragraphs. Analyze symptoms carefully, ask ONE follow-up question at a time. User says: %s",
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
