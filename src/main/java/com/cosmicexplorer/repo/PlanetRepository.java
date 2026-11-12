package com.cosmicexplorer.repo;

import com.cosmicexplorer.model.Planet;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanetRepository extends JpaRepository<Planet, String> {
    List<Planet> findAllByOrderBySortOrderAsc();
}
