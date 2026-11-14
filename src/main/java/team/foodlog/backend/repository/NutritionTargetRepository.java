package team.foodlog.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.foodlog.backend.model.NutritionTarget;

@Repository
public interface NutritionTargetRepository extends JpaRepository<NutritionTarget, Long> {

}
