package team.foodlog.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import team.foodlog.backend.model.MealEntryItem;

public interface MealEntryItemRepository extends JpaRepository<MealEntryItem, Long> {

    List<MealEntryItem> findAllByMealEntryId(Long mealEntryId);

}
