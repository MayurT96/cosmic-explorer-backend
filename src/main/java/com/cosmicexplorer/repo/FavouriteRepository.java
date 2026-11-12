package com.cosmicexplorer.repo;

import com.cosmicexplorer.model.Favourite;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavouriteRepository extends JpaRepository<Favourite, Long> {
    List<Favourite> findByUserId(Long userId);

    boolean existsByUserIdAndBodyId(Long userId, String bodyId);

    void deleteByUserIdAndBodyId(Long userId, String bodyId);
}
