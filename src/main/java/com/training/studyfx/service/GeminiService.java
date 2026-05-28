package com.training.studyfx.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeminiService {

    // Thay YOUR_API_KEY bằng key thật của bạn
    private static final String KEY = "YOUR_GEMINI_API_KEY";
    private static final String URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key="
            + KEY;

    private final HttpClient client = HttpClient.newHttpClient();

    public String generateResponse(String prompt) {
        try {
            String escapedPrompt = prompt
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n");

            String json = "{"
                    + "\"contents\":[{"
                    + "\"parts\":[{\"text\":\"" + escapedPrompt + "\"}]"
                    + "}],"
                    + "\"generationConfig\":{"
                    + "\"temperature\":0.7,"
                    + "\"maxOutputTokens\":1000"
                    + "}}";

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            String body = res.body();

            // Parse text from JSON response
            int idx = body.indexOf("\"text\"");
            if (idx == -1)
                return "No response from AI";

            int colon = body.indexOf(":", idx);
            int start = body.indexOf("\"", colon + 1);
            int end = body.indexOf("\"", start + 1);

            if (start == -1 || end == -1)
                return "Parse error";

            return body.substring(start + 1, end)
                    .replace("\\n", "\n")
                    .replace("\\\"", "\"");

        } catch (Exception e) {
            return "AI Error: " + e.getMessage();
        }
    }
}