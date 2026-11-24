package team.foodlog.backend.service.nutrition.provider;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import team.foodlog.backend.service.meal.recognition.FoodRecognitionResult;
import team.foodlog.backend.service.meal.recognition.RecognizedFoodItem;

@Component
@RequiredArgsConstructor
public class NutritionInfoRequestedEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void publish(FoodRecognitionResult result, Long userId, Long mealEntryId) {
        eventPublisher.publishEvent(new NutritionInfoRequestedEvent(
                mealEntryId, userId,
                result.items().stream()
                        .collect(Collectors.toUnmodifiableMap(
                                RecognizedFoodItem::name,
                                RecognizedFoodItem::estimatedWeightGrams,
                                (first, second) -> first
                        ))
        ));
    }

    @Transactional
    public void publish(String name, BigDecimal weight, Long userId, Long mealEntryId) {
        eventPublisher.publishEvent(new NutritionInfoRequestedEvent(
                mealEntryId, userId,
                Map.of(name, weight)
        ));
    }

}
