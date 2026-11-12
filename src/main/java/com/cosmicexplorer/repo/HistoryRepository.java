package com.cosmicexplorer.repo;

import com.cosmicexplorer.model.HistoryEntry;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<HistoryEntry, Long> {
    List<HistoryEntry> findTop30ByUserIdOrderByCreatedMsDesc(Long userId);

    Optional<HistoryEntry> findFirstByUserIdOrderByCreatedMsDesc(Long userId);

    void deleteByUserId(Long userId);
}
