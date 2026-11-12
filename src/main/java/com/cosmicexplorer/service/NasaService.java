package com.cosmicexplorer.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

/** Fetches live data from NASA. Results are cached for an hour so the free API limits are not hit. */
@Service
public class NasaService {

    private record Cached(Instant at, JsonNode value) { }

    private final RestClient http = RestClient.create();
    private final Map<String, Cached> cache = new ConcurrentHashMap<>();
    private final String nasaKey;

    public NasaService(@Value("${app.nasa.api-key}") String nasaKey) {
        this.nasaKey = nasaKey;
    }

    /** Astronomy Picture of the Day. */
    public JsonNode apod() {
        return cached("apod", () -> http.get()
                .uri("https://api.nasa.gov/planetary/apod?api_key={key}", nasaKey)
                .retrieve()
                .body(JsonNode.class));
    }

    /** Newest confirmed exoplanets from the NASA Exoplanet Archive (TAP service). */
    public JsonNode exoplanets(int limit) {
        int n = Math.max(1, Math.min(limit, 100));
        String adql = "select top " + n + " pl_name,hostname,pl_rade,pl_orbper,pl_orbsmax,sy_dist,disc_year "
                + "from pscomppars where pl_rade is not null and pl_orbper is not null order by disc_year desc";
        return cached("exo-" + n, () -> http.get()
                .uri("https://exoplanetarchive.ipac.caltech.edu/TAP/sync?query={q}&format=json", adql)
                .retrieve()
                .body(JsonNode.class));
    }

    private JsonNode cached(String key, Supplier<JsonNode> loader) {
        Cached hit = cache.get(key);
        if (hit != null && hit.at().plus(Duration.ofHours(1)).isAfter(Instant.now())) {
            return hit.value();
        }
        try {
            JsonNode value = loader.get();
            cache.put(key, new Cached(Instant.now(), value));
            return value;
        } catch (RestClientException e) {
            if (hit != null) return hit.value(); // an old answer is better than an error
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "NASA data could not be fetched right now.");
        }
    }
}
