package com.cosmicexplorer.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

/**
 * Answers space questions by calling the Google Gemini API.
 * If the chosen model is missing or busy, it retries and then tries a few backup models.
 */
@Service
public class AssistantService {

    private static final Logger log = LoggerFactory.getLogger(AssistantService.class);

    private static final String SYSTEM_PROMPT =
            "You are the assistant inside Cosmic Explorer AI, a college project about the Solar System and exoplanets. "
                    + "Answer questions about space, astronomy and space missions in simple words that a college student can follow. "
                    + "Reply in the same language the user writes in: English, Hindi or Marathi (in Devanagari script), "
                    + "or Hinglish / Marathi written in English letters (then reply in the same style). "
                    + "If the user asks for a particular language, use that language. Keep scientific terms like planet names understandable. "
                    + "Keep answers under 120 words. Use plain text without markdown. "
                    + "If a question is not about space or science, politely say you can only help with space topics. "
                    + "If you are not sure about a fact, say so instead of guessing.";

    private static final String SIMPLE_NOTE =
            " Explain it like the reader is 10 years old: very short sentences, everyday words and one small example.";

    /** Tried in this order after the model from the settings. Google renames models, so keep a few. */
    private static final List<String> BACKUP_MODELS =
            List.of("gemini-flash-latest", "gemini-2.5-flash-lite", "gemini-3.5-flash-lite");

    private static final int ATTEMPTS_PER_MODEL = 2;
    private static final long WAIT_BETWEEN_ATTEMPTS_MS = 1500;

    private final RestClient http = RestClient.create();
    private final String apiKey;
    private final String model;

    public AssistantService(@Value("${app.gemini.api-key}") String apiKey,
                            @Value("${app.gemini.model}") String model) {
        this.apiKey = apiKey;
        this.model = model;
    }

    public String answer(String question, boolean simple) {
        if (apiKey == null || apiKey.isBlank()) {
            return "The AI assistant is not switched on yet. Add a GEMINI_API_KEY to the backend settings to enable it.";
        }
        Map<String, Object> body = Map.of(
                "systemInstruction", Map.of("parts", List.of(Map.of("text", simple ? SYSTEM_PROMPT + SIMPLE_NOTE : SYSTEM_PROMPT))),
                "contents", List.of(Map.of("role", "user", "parts", List.of(Map.of("text", question)))),
                "generationConfig", Map.of("maxOutputTokens", 1500, "temperature", 0.4));

        Set<String> models = new LinkedHashSet<>();
        if (model != null && !model.isBlank()) {
            models.add(model.trim());
        }
        models.addAll(BACKUP_MODELS);

        int lastStatus = 0;
        for (String m : models) {
            for (int attempt = 1; attempt <= ATTEMPTS_PER_MODEL; attempt++) {
                try {
                    String text = callGemini(m, body);
                    return text.isBlank() ? "The AI did not return an answer. Try rephrasing your question." : text.trim();
                } catch (RestClientResponseException e) {
                    int code = e.getStatusCode().value();
                    lastStatus = code;
                    log.warn("Gemini model {} returned HTTP {} (attempt {}): {}", m, code, attempt, shorten(e.getResponseBodyAsString()));
                    if (code == 404) {
                        break; // this model name is not available, go to the next model
                    }
                    if (code == 400 || code == 401 || code == 403) {
                        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                                "The AI service rejected the request (" + code + "). Check that GEMINI_API_KEY is correct and active.");
                    }
                    if (attempt < ATTEMPTS_PER_MODEL) {
                        pause();
                    }
                } catch (RestClientException e) {
                    log.warn("Gemini could not be reached: {}", e.getMessage());
                    throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                            "The AI service could not be reached. Check the internet connection.");
                }
            }
        }

        String msg;
        if (lastStatus == 429) {
            msg = "Too many questions right now (limit reached). Wait a minute and try again.";
        } else if (lastStatus == 404) {
            msg = "No Gemini model was available. Set GEMINI_MODEL to a model name that is listed in Google AI Studio.";
        } else {
            msg = "Google's AI service is busy right now (" + lastStatus + "). Please try again in a minute.";
        }
        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, msg);
    }

    private String callGemini(String modelName, Map<String, Object> body) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent";
        JsonNode reply = http.post()
                .uri(url)
                .header("x-goog-api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(JsonNode.class);
        return reply == null ? "" : reply.path("candidates").path(0).path("content")
                .path("parts").path(0).path("text").asText("");
    }

    private static void pause() {
        try {
            Thread.sleep(WAIT_BETWEEN_ATTEMPTS_MS);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "The request was interrupted. Please try again.");
        }
    }

    private static String shorten(String s) {
        if (s == null) {
            return "";
        }
        String t = s.replaceAll("\\s+", " ").trim();
        return t.length() > 300 ? t.substring(0, 300) + "..." : t;
    }
}