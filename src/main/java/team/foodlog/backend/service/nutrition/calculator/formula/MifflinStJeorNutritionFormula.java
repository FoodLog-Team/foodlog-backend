package team.foodlog.backend.service.nutrition.calculator.formula;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import team.foodlog.backend.dto.nutrition.profile.UserNutritionProfileDto;
import team.foodlog.backend.model.enums.Sex;
import team.foodlog.backend.service.nutrition.enums.NutritionFormulaType;

@Component
@RequiredArgsConstructor
public class MifflinStJeorNutritionFormula
        extends AbstractNutritionFormula<BasicNutritionFormulaArguments> {

    public static final BigDecimal WEIGHT_MULTIPLIER = BigDecimal.valueOf(10);

    public static final BigDecimal HEIGHT_MULTIPLIER = BigDecimal.valueOf(6.25);

    public static final BigDecimal AGE_MULTIPLIER = BigDecimal.valueOf(5);

    public static final BigDecimal MALE_ADJUSTMENT = BigDecimal.valueOf(5);

    public static final BigDecimal FEMALE_ADJUSTMENT = BigDecimal.valueOf(-161);

    private final Clock clock;

    @Override
    public NutritionFormulaType getType() {
        return NutritionFormulaType.MIFFLIN_ST_JEOR;
    }

    @Override
    protected Integer apply(BasicNutritionFormulaArguments arguments) {
        BigDecimal height = BigDecimal.valueOf(arguments.height());
        BigDecimal age = BigDecimal.valueOf(arguments.age());

        BigDecimal adjustment = arguments.sex() == Sex.MALE
                ? MALE_ADJUSTMENT
                : FEMALE_ADJUSTMENT;

        return WEIGHT_MULTIPLIER.multiply(arguments.weight())
                .add(HEIGHT_MULTIPLIER.multiply(height))
                .subtract(AGE_MULTIPLIER.multiply(age))
                .add(adjustment)
                .setScale(0, RoundingMode.HALF_UP)
                .intValueExact();
    }

    @Override
    protected BasicNutritionFormulaArguments createArguments(UserNutritionProfileDto dto) {
        Objects.requireNonNull(dto);

        LocalDate dateOfBirth = Objects.requireNonNull(dto.dateOfBirth());
        int age = Period.between(dateOfBirth, LocalDate.now(clock)).getYears();

        return new BasicNutritionFormulaArguments(
            Objects.requireNonNull(dto.weight()),
            Objects.requireNonNull(dto.height()),
            age,
            Objects.requireNonNull(dto.sex())
        );
    }
}
