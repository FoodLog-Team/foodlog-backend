package team.foodlog.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.foodlog.backend.model.UserNutritionProfile;

@Repository
public interface UserNutritionProfileRepository extends JpaRepository<UserNutritionProfile, Long> {

}
