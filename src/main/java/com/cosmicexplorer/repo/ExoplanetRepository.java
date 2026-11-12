package com.cosmicexplorer.repo;

import com.cosmicexplorer.model.Exoplanet;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExoplanetRepository extends JpaRepository<Exoplanet, String> {
    List<Exoplanet> findAllByOrderBySortOrderAsc();
}
