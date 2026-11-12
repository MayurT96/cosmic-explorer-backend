package com.cosmicexplorer.web;

import com.cosmicexplorer.service.NasaService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Live NASA data passed through the backend so the API key stays on the server. */
@RestController
@RequestMapping("/api/nasa")
public class NasaController {

    private final NasaService nasa;

    public NasaController(NasaService nasa) {
        this.nasa = nasa;
    }

    @GetMapping("/apod")
    public JsonNode apod() {
        return nasa.apod();
    }

    @GetMapping("/exoplanets")
    public JsonNode exoplanets(@RequestParam(defaultValue = "30") int limit) {
        return nasa.exoplanets(limit);
    }
}
