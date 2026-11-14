package team.foodlog.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import team.foodlog.backend.model.MealEntryPhoto;

@Repository
public interface MealEntryPhotoRepository extends JpaRepository<MealEntryPhoto, Long> {

}
