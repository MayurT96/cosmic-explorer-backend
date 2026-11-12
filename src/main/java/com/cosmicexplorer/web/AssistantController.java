package com.cosmicexplorer.web;

import com.cosmicexplorer.service.AssistantService;
import com.cosmicexplorer.web.Dtos.ChatRequest;
import com.cosmicexplorer.web.Dtos.ChatResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assistant")
public class AssistantController {

    private final AssistantService assistant;

    public AssistantController(AssistantService assistant) {
        this.assistant = assistant;
    }

    @PostMapping("/chat")
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        return new ChatResponse(assistant.answer(request.question().trim(), Boolean.TRUE.equals(request.simple())));
    }
}
