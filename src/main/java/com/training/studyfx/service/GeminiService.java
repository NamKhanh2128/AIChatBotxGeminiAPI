package com.training.studyfx.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

public class GeminiService {

    private static String KEY = "YOUR_GEMINI_API_KEY";
    private static String MODEL = "gemini-1.5-flash";
    private static String URL;

    static {
        try (InputStream input = GeminiService.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                Properties prop = new Properties();
                prop.load(input);
                String val = prop.getProperty("gemini.api.key");
                if (val != null && !val.trim().isEmpty() && !val.trim().equals("YOUR_GEMINI_API_KEY")) {
                    KEY = val.trim();
                }
                String modelVal = prop.getProperty("gemini.model");
                if (modelVal != null && !modelVal.trim().isEmpty()) {
                    MODEL = modelVal.trim();
                }
            } else {
                System.err.println("Warning: config.properties not found in classpath resources!");
            }
        } catch (Exception e) {
            System.err.println("Error loading config.properties: " + e.getMessage());
        }
        URL = "https://generativelanguage.googleapis.com/v1beta/models/" + MODEL + ":generateContent?key=" + KEY;
    }

    private final HttpClient client = HttpClient.newHttpClient();

    /** Models to try in order if the primary model is overloaded. */
    private static final String[] FALLBACK_MODELS = {
        "gemini-flash-latest",
        "gemini-2.0-flash"
    };

    public String generateResponse(String prompt) {
        // Try primary model with retries, then fallback models
        String result = tryModel(URL, prompt, 3);
        if (result != null) return result;

        for (String fallback : FALLBACK_MODELS) {
            String fallbackUrl = "https://generativelanguage.googleapis.com/v1beta/models/"
                    + fallback + ":generateContent?key=" + KEY;
            System.out.println("GeminiService: falling back to " + fallback);
            result = tryModel(fallbackUrl, prompt, 1);
            if (result != null) return result;
        }

        return "⚠️ AI đang bận, vui lòng thử lại sau.";
    }

    /**
     * Try calling a specific model URL up to maxRetries times.
     * Returns the AI reply text on success, null if all retries fail due to overload.
     */
    private String tryModel(String url, String prompt, int maxRetries) {
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

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
                String body = res.body();
                int statusCode = res.statusCode();

                // Overloaded / rate-limited → retry with backoff
                if (statusCode == 503 || statusCode == 429
                        || body.contains("high demand")
                        || body.contains("RESOURCE_EXHAUSTED")) {
                    System.out.println("GeminiService: attempt " + attempt + " overloaded (HTTP "
                            + statusCode + "), retrying...");
                    if (attempt < maxRetries) {
                        Thread.sleep(1500L * attempt); // 1.5s, 3s backoff
                        continue;
                    }
                    return null; // caller will try next fallback
                }

                // Parse JSON response
                try {
                    JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();

                    if (jsonObject.has("error")) {
                        JsonObject error = jsonObject.getAsJsonObject("error");
                        String msg = error.has("message") ? error.get("message").getAsString() : body;
                        // Overloaded error inside JSON body
                        if (msg.contains("high demand") || msg.contains("RESOURCE_EXHAUSTED")) {
                            if (attempt < maxRetries) {
                                Thread.sleep(1500L * attempt);
                                continue;
                            }
                            return null;
                        }
                        return "API Error: " + msg;
                    }

                    if (jsonObject.has("candidates")) {
                        JsonObject candidate = jsonObject.getAsJsonArray("candidates")
                                .get(0).getAsJsonObject();
                        if (candidate.has("content")) {
                            JsonObject content = candidate.getAsJsonObject("content");
                            if (content.has("parts")) {
                                JsonObject part = content.getAsJsonArray("parts")
                                        .get(0).getAsJsonObject();
                                if (part.has("text")) {
                                    return part.get("text").getAsString();
                                }
                            }
                        }
                    }
                    return "No response from AI.";
                } catch (Exception parseEx) {
                    return "Parse error: " + parseEx.getMessage();
                }

            } catch (Exception e) {
                return "AI Error: " + e.getMessage();
            }
        }
        return null;
    }
}