package com.cosmicexplorer.service;

import com.cosmicexplorer.model.Exoplanet;
import com.cosmicexplorer.model.Mission;
import com.cosmicexplorer.model.Planet;
import com.cosmicexplorer.model.QuizQuestion;
import com.cosmicexplorer.repo.ExoplanetRepository;
import com.cosmicexplorer.repo.MissionRepository;
import com.cosmicexplorer.repo.PlanetRepository;
import com.cosmicexplorer.repo.QuizQuestionRepository;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * Fills the tables from src/main/resources/seed/*.json the first time the server starts.
 * A table that already has rows is left alone, so edits you make in the database stay.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final PlanetRepository planets;
    private final ExoplanetRepository exoplanets;
    private final MissionRepository missions;
    private final QuizQuestionRepository quiz;

    // reads JSON straight into the entity fields (the entities have no setters)
    private final ObjectMapper mapper = new ObjectMapper()
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public DataSeeder(PlanetRepository planets, ExoplanetRepository exoplanets,
                      MissionRepository missions, QuizQuestionRepository quiz) {
        this.planets = planets;
        this.exoplanets = exoplanets;
        this.missions = missions;
        this.quiz = quiz;
    }

    @Override
    public void run(String... args) throws Exception {
        if (planets.count() == 0) {
            List<Planet> list = read("seed/planets.json", new TypeReference<List<Planet>>() { });
            for (int i = 0; i < list.size(); i++) list.get(i).setSortOrder(i);
            planets.saveAll(list);
            log.info("Seeded {} planets", list.size());
        }
        if (exoplanets.count() == 0) {
            List<Exoplanet> list = read("seed/exoplanets.json", new TypeReference<List<Exoplanet>>() { });
            for (int i = 0; i < list.size(); i++) list.get(i).setSortOrder(i);
            exoplanets.saveAll(list);
            log.info("Seeded {} exoplanets", list.size());
        }
        if (missions.count() == 0) {
            List<Mission> list = read("seed/missions.json", new TypeReference<List<Mission>>() { });
            for (int i = 0; i < list.size(); i++) list.get(i).setSortOrder(i);
            missions.saveAll(list);
            log.info("Seeded {} missions", list.size());
        }
        if (quiz.count() == 0) {
            List<QuizQuestion> list = read("seed/quiz.json", new TypeReference<List<QuizQuestion>>() { });
            for (int i = 0; i < list.size(); i++) list.get(i).setSortOrder(i);
            quiz.saveAll(list);
            log.info("Seeded {} quiz questions", list.size());
        }
    }

    private <T> List<T> read(String path, TypeReference<List<T>> type) throws Exception {
        try (InputStream in = new ClassPathResource(path).getInputStream()) {
            return mapper.readValue(in, type);
        }
    }
}
