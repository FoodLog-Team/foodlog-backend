package team.foodlog.backend.service.nutrition.calculator;

import team.foodlog.backend.dto.nutrition.profile.UserNutritionProfileDto;
import team.foodlog.backend.service.nutrition.enums.NutritionFormulaType;
import team.foodlog.backend.service.nutrition.enums.NutritionTargetCalculationType;

public interface NutritionTargetCalculator {

    NutritionTargetCalculationType getType();

    NutritionTargetCalculationResult calculate(
            UserNutritionProfileDto dto, NutritionFormulaType formulaType);

}
