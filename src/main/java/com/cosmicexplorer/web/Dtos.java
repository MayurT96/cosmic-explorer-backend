package com.cosmicexplorer.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Request and response shapes for the API. */
public final class Dtos {

    private Dtos() {
    }

    public record RegisterRequest(
            @NotBlank @Size(max = 60) String name,
            @NotBlank @Email @Size(max = 120) String email,
            @NotBlank @Size(min = 6, max = 100) String password) { }

    public record LoginRequest(@NotBlank String email, @NotBlank String password) { }

    public record AuthResponse(String name, String token) { }

    public record MeResponse(String name, String email, int bestScore) { }

    public record HistoryItem(String kind, String text, long at) { }

    public record HistoryRequest(@NotBlank @Size(max = 40) String kind, @NotBlank @Size(max = 300) String text) { }

    public record BestRequest(@Min(0) @Max(100) int score) { }

    public record ChatRequest(@NotBlank @Size(max = 300) String question, Boolean simple) { }

    public record ChatResponse(String answer) { }
}
