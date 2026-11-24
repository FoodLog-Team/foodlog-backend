package team.foodlog.backend.service.meal.recognition;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import team.foodlog.backend.config.AsyncConfig;
import team.foodlog.backend.exception.InvalidPhotoException;
import team.foodlog.backend.model.MealEntryPhoto;
import team.foodlog.backend.model.enums.MealEntryProcessingError;
import team.foodlog.backend.repository.MealEntryPhotoRepository;
import team.foodlog.backend.service.meal.MealEntryService;
import team.foodlog.backend.service.nutrition.provider.NutritionInfoRequestedEventPublisher;

@Component
@RequiredArgsConstructor
@Slf4j
public class FoodRecognitionRequestedEventProcessor {

    private final FoodRecognitionService foodRecognitionService;

    private final MealEntryService mealEntryService;

    private final NutritionInfoRequestedEventPublisher publisher;

    private final MealEntryPhotoRepository mealEntryPhotoRepository;

    @Async(AsyncConfig.AI_REQUEST_EXECUTOR_BEAN_NAME)
    public void process(FoodRecognitionRequestedEvent event) {
        log.debug("AI processing started: {}", event.mealEntryId());

        mealEntryService.markProcessing(event.mealEntryId(), event.userId());

        try {
            List<MealEntryPhoto> photos = mealEntryPhotoRepository
                    .findAllById(event.photoIds());

            log.debug("Gemini recognition started: {}", event.mealEntryId());

            FoodRecognitionResult result =
                    foodRecognitionService.recognize(photos, event.plateSize());

            log.debug("Gemini recognition completed: {}", event.mealEntryId());

            switch (result.status()) {
                case NO_FOOD -> {
                    mealEntryService.markFailed(
                            event.mealEntryId(), event.userId(),
                            MealEntryProcessingError.NO_FOOD_DETECTED
                    );
                    throw new InvalidPhotoException("No food on the photo was detected");
                }
                case NOT_RECOGNIZED -> {
                    mealEntryService.markFailed(
                            event.mealEntryId(), event.userId(),
                            MealEntryProcessingError.FOOD_NOT_RECOGNIZED
                    );
                    throw new InvalidPhotoException("The food on photo was not recognized");
                }
                case null ->
                    throw new IllegalStateException("Validated recognition status is null");

                default -> {

                }
            }

            mealEntryService.updateAiConfidence(
                    event.mealEntryId(), event.userId(), result.aiConfidence()
            );

            log.debug("Nutrition processing started: {}", event.mealEntryId());

            log.debug("Publishing nutrition event for meal entry {}", event.mealEntryId());
            publisher.publish(result, event.userId(), event.mealEntryId());
            log.debug("Nutrition event published for meal entry {}", event.mealEntryId());
        } catch (InvalidPhotoException e) {
            throw e;
        } catch (Exception e) {
            log.error("Food recognition failed for meal entry {}", event.mealEntryId(), e);
            mealEntryService.markFailed(
                    event.mealEntryId(),
                    event.userId(),
                    MealEntryProcessingError.NUTRITION_FETCHING_FAILED
            );
            throw e;
        }
    }

}
