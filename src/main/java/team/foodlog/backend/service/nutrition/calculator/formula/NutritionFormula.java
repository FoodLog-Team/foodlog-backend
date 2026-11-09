package team.foodlog.backend.service.nutrition.calculator.formula;

import team.foodlog.backend.dto.nutrition.profile.UserNutritionProfileDto;
import team.foodlog.backend.service.nutrition.enums.NutritionFormulaType;

public interface NutritionFormula {

    NutritionFormulaType getType();

    Integer calculate(UserNutritionProfileDto dto);

}
