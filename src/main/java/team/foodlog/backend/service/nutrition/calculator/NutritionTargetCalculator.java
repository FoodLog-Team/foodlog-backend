package team.foodlog.backend.service.nutrition.calculator;

import java.util.function.Function;

@FunctionalInterface
public interface NutritionTargetCalculator<T extends NutritionTargetCalculationArguments>
        extends Function<T, Integer> {

    Integer apply(T arguments);

}
