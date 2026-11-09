package team.foodlog.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.foodlog.backend.model.UserNutritionProfile;

public interface UserNutritionProfileRepository extends JpaRepository<UserNutritionProfile, Long> {

}
