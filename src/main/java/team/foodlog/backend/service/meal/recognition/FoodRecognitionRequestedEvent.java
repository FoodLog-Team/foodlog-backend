package team.foodlog.backend.service.meal.recognition;

import java.util.List;
import team.foodlog.backend.model.enums.PlateSize;

public record FoodRecognitionRequestedEvent(
        Long mealEntryId,
        Long userId,
        List<Long> photoIds,
        PlateSize plateSize
) {}
