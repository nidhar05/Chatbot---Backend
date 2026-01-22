package com.chatbot.demo.service;

public class PrakrutiAnalyzer {

    public static String assess(String text) {

        text = text.toLowerCase();

        int vata = 0, pitta = 0, kapha = 0;

        // Vata indicators
        if (text.contains("dry") || text.contains("cracking") || text.contains("stiff")) vata++;
        if (text.contains("pain") || text.contains("joint") || text.contains("muscle")) vata++;

        // Pitta indicators
        if (text.contains("burning") || text.contains("heat") || text.contains("inflammation")) pitta++;

        // Kapha indicators
        if (text.contains("heavy") || text.contains("swelling") || text.contains("slow")) kapha++;

        if (vata >= pitta && vata >= kapha) return "Vata Prakruti";
        if (pitta >= vata && pitta >= kapha) return "Pitta Prakruti";
        return "Kapha Prakruti";
    }
}
