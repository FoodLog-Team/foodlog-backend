package team.foodlog.backend.mapper;

import org.springframework.stereotype.Component;
import team.foodlog.backend.dto.meal.MealEntryItemResponseDto;
import team.foodlog.backend.model.MealEntryItem;
import team.foodlog.backend.model.enums.MealItemFetchingStatus;
import team.foodlog.backend.service.nutrition.NutritionCalculator;
import team.foodlog.backend.service.nutrition.calculator.NutritionInfo;

@Component
public class MealEntryItemMapper {

    public MealEntryItemResponseDto toResponse(MealEntryItem item) {
        if (item.getStatus() == MealItemFetchingStatus.FAILED) {
            return new MealEntryItemResponseDto(
                    item.getName(),
                    item.getGrams(),
                    null,
                    null,
                    item.getStatus()
            );
        }

        NutritionInfo per100g = new NutritionInfo(
                item.getKcalPer100g(),
                item.getCarbsPer100g(),
                item.getProteinPer100g(),
                item.getFatPer100g()
        );

        return new MealEntryItemResponseDto(
                item.getName(), item.getGrams(),
                NutritionCalculator.calculateTotal(item.getGrams(), per100g),
                per100g,
                item.getStatus()
        );
    }

}
