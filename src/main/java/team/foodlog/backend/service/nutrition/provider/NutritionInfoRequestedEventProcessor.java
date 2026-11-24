package team.foodlog.backend.service.nutrition.provider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import team.foodlog.backend.exception.NutritionInfoNotFoundException;
import team.foodlog.backend.model.enums.MealEntryProcessingError;
import team.foodlog.backend.service.meal.MealEntryItemService;
import team.foodlog.backend.service.meal.MealEntryService;

@Component
@RequiredArgsConstructor
@Slf4j
public class NutritionInfoRequestedEventProcessor {

    private final NutritionInfoProviderChain providerChain;

    private final MealEntryItemService mealEntryItemService;

    private final MealEntryService mealEntryService;

    public void process(NutritionInfoRequestedEvent event) {
        List<CompletableFuture<Void>> futures =
                event.mealNameToWeight().entrySet().stream()
                        .map(entry -> providerChain.provide(entry.getKey())
                                .exceptionally(exception -> {
                                    log.error(
                                            "Provider failed for food '{}'",
                                            entry.getKey(),
                                            exception
                                    );
                                    Throwable cause = unwrapException(exception);

                                    if (cause instanceof NutritionInfoNotFoundException) {
                                        return null;
                                    }

                                    throw new CompletionException(cause);
                                })
                                .thenAccept(nutritionInfo -> {
                                    if (nutritionInfo == null) {
                                        mealEntryItemService.createFailed(
                                                event.userId(),
                                                entry.getKey(),
                                                entry.getValue(),
                                                event.mealEntryId()
                                        );
                                        return;
                                    }

                                    mealEntryItemService.create(
                                            event.userId(),
                                            nutritionInfo,
                                            entry.getKey(),
                                            entry.getValue(),
                                            event.mealEntryId()
                                    );
                                })
                        ).toList();

        CompletableFuture.allOf(
                futures.toArray(CompletableFuture[]::new)
        ).whenComplete((result, exception) -> {
            if (exception != null) {
                log.error(
                        "Nutrition processing failed for meal entry {}",
                        event.mealEntryId(),
                        exception
                );
                mealEntryService.markFailed(
                        event.mealEntryId(), event.userId(),
                        MealEntryProcessingError.NUTRITION_FETCHING_FAILED
                );
                return;
            }

            mealEntryService.markCompleted(event.mealEntryId(), event.userId());
        });

    }

    private Throwable unwrapException(Throwable exception) {
        while ((exception instanceof CompletionException
                || exception instanceof ExecutionException)
                && exception.getCause() != null) {
            exception = exception.getCause();
        }

        return exception;
    }

}
