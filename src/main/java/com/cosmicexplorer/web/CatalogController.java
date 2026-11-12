package com.cosmicexplorer.web;

import com.cosmicexplorer.model.Exoplanet;
import com.cosmicexplorer.model.Mission;
import com.cosmicexplorer.model.Planet;
import com.cosmicexplorer.model.QuizQuestion;
import com.cosmicexplorer.repo.ExoplanetRepository;
import com.cosmicexplorer.repo.MissionRepository;
import com.cosmicexplorer.repo.PlanetRepository;
import com.cosmicexplorer.repo.QuizQuestionRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** Public, read-only data: planets, exoplanets, missions and quiz questions. */
@RestController
@RequestMapping("/api")
public class CatalogController {

    private final PlanetRepository planets;
    private final ExoplanetRepository exoplanets;
    private final MissionRepository missions;
    private final QuizQuestionRepository quiz;

    public CatalogController(PlanetRepository planets, ExoplanetRepository exoplanets,
                             MissionRepository missions, QuizQuestionRepository quiz) {
        this.planets = planets;
        this.exoplanets = exoplanets;
        this.missions = missions;
        this.quiz = quiz;
    }

    @GetMapping("/health")
    public java.util.Map<String, String> health() {
        return java.util.Map.of("status", "ok");
    }

    @GetMapping("/planets")
    public List<Planet> planets() {
        return planets.findAllByOrderBySortOrderAsc();
    }

    @GetMapping("/planets/{id}")
    public Planet planet(@PathVariable String id) {
        return planets.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No planet with id " + id));
    }

    @GetMapping("/exoplanets")
    public List<Exoplanet> exoplanets() {
        return exoplanets.findAllByOrderBySortOrderAsc();
    }

    @GetMapping("/missions")
    public List<Mission> missions() {
        return missions.findAllByOrderBySortOrderAsc();
    }

    @GetMapping("/quiz")
    public List<QuizQuestion> quiz() {
        return quiz.findAllByOrderBySortOrderAsc();
    }
}
