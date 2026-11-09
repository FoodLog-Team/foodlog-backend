package team.foodlog.backend.service.nutrition.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import org.springframework.stereotype.Component;
import team.foodlog.backend.config.NutritionConstants;
import team.foodlog.backend.dto.nutrition.profile.UserNutritionProfileDto;
import team.foodlog.backend.service.nutrition.calculator.formula.NutritionFormulaRegistry;
import team.foodlog.backend.service.nutrition.enums.NutritionTargetCalculationType;

@Component
public class BasicNutritionTargetCalculator
        extends AbstractNutritionTargetCalculator<BasicNutritionTargetCalculationArguments> {

    public BasicNutritionTargetCalculator(NutritionFormulaRegistry formulaRegistry) {
        super(formulaRegistry);
    }

    @Override
    public NutritionTargetCalculationType getType() {
        return NutritionTargetCalculationType.BASIC;
    }

    @Override
    protected NutritionTargetCalculationResult apply(
            BasicNutritionTargetCalculationArguments arguments
    ) {
        BigDecimal kcal = BigDecimal.valueOf(arguments.basalMetabolicRate())
                .multiply(arguments.activityLevelMultiplier())
                .multiply(arguments.weightGoalMultiplier());

        BigDecimal carbs = kcal.multiply(CARBS_RATIO)
                .divide(NutritionConstants.KCAL_PER_GRAM_OF_CARBS, RoundingMode.HALF_UP);

        BigDecimal protein = kcal.multiply(PROTEIN_RATIO)
                .divide(NutritionConstants.KCAL_PER_GRAM_OF_PROTEIN, RoundingMode.HALF_UP);

        BigDecimal fats = kcal.multiply(FATS_RATIO)
                .divide(NutritionConstants.KCAL_PER_GRAM_OF_FAT, RoundingMode.HALF_UP);

        return new NutritionTargetCalculationResult(
                bigDecimalToInteger(kcal),
                bigDecimalToInteger(carbs),
                bigDecimalToInteger(protein),
                bigDecimalToInteger(fats)
        );
    }

    @Override
    protected BasicNutritionTargetCalculationArguments createArguments(UserNutritionProfileDto dto,
            Integer basalMetabolicRate) {
        Objects.requireNonNull(dto);

        return new BasicNutritionTargetCalculationArguments(
                Objects.requireNonNull(basalMetabolicRate),
                Objects.requireNonNull(dto.activityLevel()).getMultiplier(),
                Objects.requireNonNull(dto.weightGoal()).getMultiplier()
        );
    }

    private Integer bigDecimalToInteger(BigDecimal value) {
        return value.setScale(0, RoundingMode.HALF_UP).intValueExact();
    }
}
