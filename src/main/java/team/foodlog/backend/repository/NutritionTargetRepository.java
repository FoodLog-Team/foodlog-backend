package team.foodlog.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.foodlog.backend.model.NutritionTarget;

public interface NutritionTargetRepository extends JpaRepository<NutritionTarget, Long> {

}
