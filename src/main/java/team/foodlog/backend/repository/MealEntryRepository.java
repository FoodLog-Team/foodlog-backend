package team.foodlog.backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.foodlog.backend.model.MealEntry;

@Repository
public interface MealEntryRepository extends JpaRepository<MealEntry, Long> {

    boolean existsByIdAndUserId(Long id, Long userId);

    Optional<MealEntry> findByIdAndUserId(Long id, Long userId);

}
