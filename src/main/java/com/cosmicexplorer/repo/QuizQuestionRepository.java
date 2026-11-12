package com.cosmicexplorer.repo;

import com.cosmicexplorer.model.QuizQuestion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    List<QuizQuestion> findAllByOrderBySortOrderAsc();
}
