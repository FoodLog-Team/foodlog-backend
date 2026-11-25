package team.foodlog.backend.service.meal;

import java.math.BigDecimal;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.foodlog.backend.dto.meal.CreateMealEntryItemRequestDto;
import team.foodlog.backend.exception.EntityNotFoundException;
import team.foodlog.backend.model.MealEntryItem;
import team.foodlog.backend.model.enums.MealItemFetchingStatus;
import team.foodlog.backend.repository.MealEntryItemRepository;
import team.foodlog.backend.repository.MealEntryRepository;
import team.foodlog.backend.security.common.AuthenticatedUserIdSupplier;
import team.foodlog.backend.service.nutrition.calculator.NutritionInfo;
import team.foodlog.backend.service.nutrition.provider.NutritionInfoRequestedEventPublisher;

@Service
@RequiredArgsConstructor
public class MealEntryItemService {

    private final MealEntryItemRepository mealEntryItemRepository;

    private final MealEntryRepository mealEntryRepository;

    private final MealEntryService mealEntryService;

    private final NutritionInfoRequestedEventPublisher eventPublisher;

    private final AuthenticatedUserIdSupplier userIdSupplier;

    @Transactional
    public void create(
            Long userId,
            NutritionInfo nutritionInfo,
            String name,
            BigDecimal weight,
            Long mealEntryId
    ) {
        createMealEntryItem(
                userId, nutritionInfo, name, weight,
                mealEntryId, MealItemFetchingStatus.COMPLETED
        );
    }

    @Transactional
    public void create(Long mealEntryId, CreateMealEntryItemRequestDto requestDto) {
        Long userId = userIdSupplier.get();
        mealEntryService.markProcessing(mealEntryId, userId);

        eventPublisher.publish(
                requestDto.name(), requestDto.weight(),
                userId, mealEntryId
        );
    }

    @Transactional
    public void createFailed(
            Long userId,
            String name,
            BigDecimal weight,
            Long mealEntryId
    ) {
        createMealEntryItem(
                userId, null, name, weight,
                mealEntryId, MealItemFetchingStatus.FAILED);
    }

    private void createMealEntryItem(
            Long userId,
            NutritionInfo nutritionInfo,
            String name,
            BigDecimal weight,
            Long mealEntryId,
            MealItemFetchingStatus status
    ) {
        if (!mealEntryRepository.existsByIdAndUserId(mealEntryId, userId)) {
            throw new EntityNotFoundException("Meal entry was not found");
        }

        MealEntryItem mealEntryItem = new MealEntryItem(mealEntryId)
                .setName(name)
                .setGrams(weight)
                .setStatus(status);

        if (status == MealItemFetchingStatus.COMPLETED) {
            Objects.requireNonNull(nutritionInfo);

            mealEntryItem
                    .setKcalPer100g(nutritionInfo.kcal())
                    .setCarbsPer100g(nutritionInfo.carbs())
                    .setProteinPer100g(nutritionInfo.protein())
                    .setFatPer100g(nutritionInfo.fat());
        }

        mealEntryItemRepository.save(mealEntryItem);
    }

}
