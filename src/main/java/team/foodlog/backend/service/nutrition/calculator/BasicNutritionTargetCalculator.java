package team.foodlog.backend.service.nutrition.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class BasicNutritionTargetCalculator
        implements NutritionTargetCalculator<BasicNutritionTargetCalculationArguments> {

    @Override
    public Integer apply(BasicNutritionTargetCalculationArguments arguments) {
        Objects.requireNonNull(arguments);

        BigDecimal basalMetabolicRate =
                BigDecimal.valueOf(Objects.requireNonNull(arguments.basalMetabolicRate()));
        BigDecimal activityLevelMultiplier =
                Objects.requireNonNull(arguments.activityLevelMultiplier());
        BigDecimal weightGoalMultiplier =
                Objects.requireNonNull(arguments.weightGoalMultiplier());

        return basalMetabolicRate
                    .multiply(activityLevelMultiplier)
                    .multiply(weightGoalMultiplier)
                    .setScale(0, RoundingMode.HALF_UP)
                    .intValueExact();
    }
}
