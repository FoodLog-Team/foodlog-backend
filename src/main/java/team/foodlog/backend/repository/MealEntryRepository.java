package team.foodlog.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.foodlog.backend.model.MealEntry;

@Repository
public interface MealEntryRepository extends JpaRepository<MealEntry, Long> {

}
