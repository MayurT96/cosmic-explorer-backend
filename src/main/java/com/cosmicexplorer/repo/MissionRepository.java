package com.cosmicexplorer.repo;

import com.cosmicexplorer.model.Mission;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionRepository extends JpaRepository<Mission, Long> {
    List<Mission> findAllByOrderBySortOrderAsc();
}
