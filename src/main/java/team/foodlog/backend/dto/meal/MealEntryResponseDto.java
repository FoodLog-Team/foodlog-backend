package team.foodlog.backend.dto.meal;

import java.math.BigDecimal;
import java.util.List;
import team.foodlog.backend.model.enums.MealAnalysisStatus;
import team.foodlog.backend.model.enums.MealEntryProcessingError;
import team.foodlog.backend.service.nutrition.calculator.NutritionInfo;

public record MealEntryResponseDto(
        Long id,
        MealAnalysisStatus status,
        MealEntryProcessingError processingError,
        BigDecimal grams,
        NutritionInfo total,
        List<MealEntryItemResponseDto> items
) {}
