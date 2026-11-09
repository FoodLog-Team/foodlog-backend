package team.foodlog.backend.service.nutrition.calculator.formula;

import java.math.BigDecimal;
import team.foodlog.backend.model.enums.Sex;

public record BasicNutritionFormulaArguments(
        BigDecimal weight,
        Integer height,
        Integer age,
        Sex sex
) implements NutritionFormulaArguments {}
