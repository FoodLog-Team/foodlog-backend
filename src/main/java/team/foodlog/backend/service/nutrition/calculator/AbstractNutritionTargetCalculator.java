package team.foodlog.backend.service.nutrition.calculator;

import lombok.RequiredArgsConstructor;
import team.foodlog.backend.dto.nutrition.profile.UserNutritionProfileDto;
import team.foodlog.backend.service.nutrition.calculator.formula.NutritionFormulaRegistry;
import team.foodlog.backend.service.nutrition.enums.NutritionFormulaType;

@RequiredArgsConstructor
public abstract class AbstractNutritionTargetCalculator<
        T extends NutritionTargetCalculationArguments>
        implements NutritionTargetCalculator {

    private final NutritionFormulaRegistry formulaRegistry;

    @Override
    public final NutritionTargetCalculationResult calculate(
            UserNutritionProfileDto dto, NutritionFormulaType formulaType
    ) {
        Integer basalMetabolicRate = formulaRegistry
                .getRequired(formulaType)
                .calculate(dto);

        return apply(createArguments(dto, basalMetabolicRate));
    }

    protected abstract NutritionTargetCalculationResult apply(T arguments);

    protected abstract T createArguments(UserNutritionProfileDto dto, Integer basalMetabolicRate);

}
