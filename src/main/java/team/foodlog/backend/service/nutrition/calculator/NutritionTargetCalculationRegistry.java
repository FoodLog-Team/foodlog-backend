package team.foodlog.backend.service.nutrition.calculator;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import team.foodlog.backend.service.nutrition.enums.NutritionTargetCalculationType;

@Component
public class NutritionTargetCalculationRegistry {

    private final Map<NutritionTargetCalculationType, NutritionTargetCalculator> calculators;

    public NutritionTargetCalculationRegistry(List<NutritionTargetCalculator> calculators) {
        this.calculators = calculators.stream()
            .collect(Collectors.toUnmodifiableMap(
                    NutritionTargetCalculator::getType,
                    Function.identity()
            ));
    }

    public NutritionTargetCalculator getRequired(NutritionTargetCalculationType calculationType) {
        NutritionTargetCalculator calculator = calculators.get(calculationType);

        if (calculator == null) {
            throw new IllegalStateException("Unknown nutrition target calculation type");
        }

        return calculator;
    }

}
