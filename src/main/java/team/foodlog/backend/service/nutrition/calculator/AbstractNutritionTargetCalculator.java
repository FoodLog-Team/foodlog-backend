package team.foodlog.backend.service.nutrition.calculator;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import team.foodlog.backend.dto.nutrition.profile.UserNutritionProfileDto;
import team.foodlog.backend.service.nutrition.calculator.formula.NutritionFormulaRegistry;
import team.foodlog.backend.service.nutrition.enums.NutritionFormulaType;

@RequiredArgsConstructor
public abstract class AbstractNutritionTargetCalculator<
        T extends NutritionTargetCalculationArguments>
        implements NutritionTargetCalculator {

    public static final BigDecimal CARBS_RATIO = BigDecimal.valueOf(0.5);

    public static final BigDecimal PROTEIN_RATIO = BigDecimal.valueOf(0.3);

    public static final BigDecimal FATS_RATIO = BigDecimal.valueOf(0.2);

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
