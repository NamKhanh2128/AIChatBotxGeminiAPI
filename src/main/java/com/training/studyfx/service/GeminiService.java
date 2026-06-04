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
    private static String MODEL = "gemini-3.5-flash";
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

            try {
                JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                
                // Check if there is an error field
                if (jsonObject.has("error")) {
                    JsonObject error = jsonObject.getAsJsonObject("error");
                    return "API Error: " + (error.has("message") ? error.get("message").getAsString() : body);
                }

                // Try to navigate to candidate[0].content.parts[0].text
                if (jsonObject.has("candidates")) {
                    JsonObject candidate = jsonObject.getAsJsonArray("candidates").get(0).getAsJsonObject();
                    if (candidate.has("content")) {
                        JsonObject content = candidate.getAsJsonObject("content");
                        if (content.has("parts")) {
                            JsonObject part = content.getAsJsonArray("parts").get(0).getAsJsonObject();
                            if (part.has("text")) {
                                return part.get("text").getAsString();
                            }
                        }
                    }
                }
                return "No response from AI. Response: " + body;
            } catch (Exception parseEx) {
                return "Parse error: " + parseEx.getMessage() + ". Raw body: " + body;
            }

        } catch (Exception e) {
            return "AI Error: " + e.getMessage();
        }
    }
}