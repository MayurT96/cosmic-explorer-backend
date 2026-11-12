package com.cosmicexplorer.web;

import com.cosmicexplorer.model.AppUser;
import com.cosmicexplorer.model.Favourite;
import com.cosmicexplorer.model.HistoryEntry;
import com.cosmicexplorer.repo.FavouriteRepository;
import com.cosmicexplorer.repo.HistoryRepository;
import com.cosmicexplorer.repo.PlanetRepository;
import com.cosmicexplorer.repo.UserRepository;
import com.cosmicexplorer.web.Dtos.BestRequest;
import com.cosmicexplorer.web.Dtos.HistoryItem;
import com.cosmicexplorer.web.Dtos.HistoryRequest;
import com.cosmicexplorer.web.Dtos.MeResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** Everything here needs a valid login token. Each user only ever sees their own rows. */
@RestController
@RequestMapping("/api/me")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")
public class MeController {

    private final UserRepository users;
    private final HistoryRepository history;
    private final FavouriteRepository favourites;
    private final PlanetRepository planets;

    public MeController(UserRepository users, HistoryRepository history,
                        FavouriteRepository favourites, PlanetRepository planets) {
        this.users = users;
        this.history = history;
        this.favourites = favourites;
        this.planets = planets;
    }

    private AppUser current(Authentication auth) {
        return users.findByEmailIgnoreCase(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please sign in again"));
    }

    @GetMapping
    public MeResponse me(Authentication auth) {
        AppUser u = current(auth);
        return new MeResponse(u.getName(), u.getEmail(), u.getBestScore());
    }

    // ---------- history ----------

    @GetMapping("/history")
    public List<HistoryItem> history(Authentication auth) {
        AppUser u = current(auth);
        return history.findTop30ByUserIdOrderByCreatedMsDesc(u.getId()).stream()
                .map(h -> new HistoryItem(h.getKind(), h.getText(), h.getCreatedMs()))
                .toList();
    }

    @PostMapping("/history")
    public ResponseEntity<Void> addHistory(Authentication auth, @Valid @RequestBody HistoryRequest request) {
        AppUser u = current(auth);
        boolean repeat = history.findFirstByUserIdOrderByCreatedMsDesc(u.getId())
                .map(last -> last.getKind().equals(request.kind()) && last.getText().equals(request.text()))
                .orElse(false);
        if (!repeat) {
            history.save(new HistoryEntry(u.getId(), request.kind(), request.text(), System.currentTimeMillis()));
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/history")
    @Transactional
    public ResponseEntity<Void> clearHistory(Authentication auth) {
        history.deleteByUserId(current(auth).getId());
        return ResponseEntity.noContent().build();
    }

    // ---------- favourites ----------

    @GetMapping("/favourites")
    public List<String> favourites(Authentication auth) {
        return favourites.findByUserId(current(auth).getId()).stream().map(Favourite::getBodyId).toList();
    }

    @PutMapping("/favourites/{bodyId}")
    public ResponseEntity<Void> addFavourite(Authentication auth, @PathVariable String bodyId) {
        AppUser u = current(auth);
        if (!planets.existsById(bodyId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No planet with id " + bodyId);
        }
        if (!favourites.existsByUserIdAndBodyId(u.getId(), bodyId)) {
            favourites.save(new Favourite(u.getId(), bodyId));
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/favourites/{bodyId}")
    @Transactional
    public ResponseEntity<Void> removeFavourite(Authentication auth, @PathVariable String bodyId) {
        favourites.deleteByUserIdAndBodyId(current(auth).getId(), bodyId);
        return ResponseEntity.noContent().build();
    }

    // ---------- quiz best score ----------

    @PutMapping("/best")
    public ResponseEntity<Void> best(Authentication auth, @Valid @RequestBody BestRequest request) {
        AppUser u = current(auth);
        if (request.score() > u.getBestScore()) {
            u.setBestScore(request.score());
            users.save(u);
        }
        return ResponseEntity.noContent().build();
    }
}
