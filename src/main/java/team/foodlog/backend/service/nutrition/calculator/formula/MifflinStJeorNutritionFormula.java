package team.foodlog.backend.service.nutrition.calculator.formula;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import org.springframework.stereotype.Component;
import team.foodlog.backend.model.enums.Sex;

@Component
public class MifflinStJeorNutritionFormula
        implements NutritionFormula<BasicNutritionFormulaArguments> {

    public static final BigDecimal WEIGHT_MULTIPLIER = BigDecimal.valueOf(10);

    public static final BigDecimal HEIGHT_MULTIPLIER = BigDecimal.valueOf(6.25);

    public static final BigDecimal AGE_MULTIPLIER = BigDecimal.valueOf(5);

    @Override
    public Integer apply(BasicNutritionFormulaArguments arguments) {
        Objects.requireNonNull(arguments);

        BigDecimal weight = Objects.requireNonNull(arguments.weight());
        BigDecimal height = BigDecimal.valueOf(Objects.requireNonNull(arguments.height()));
        BigDecimal age = BigDecimal.valueOf(Objects.requireNonNull(arguments.age()));
        Sex sex = Objects.requireNonNull(arguments.sex());

        int adjustment = (sex == Sex.MALE) ? +5 : -161;

        return WEIGHT_MULTIPLIER.multiply(weight)
                    .add(HEIGHT_MULTIPLIER.multiply(height))
                    .subtract(AGE_MULTIPLIER.multiply(age))
                    .setScale(0, RoundingMode.HALF_UP)
                    .intValueExact() + adjustment;

    }
}
