package team.foodlog.backend.service.nutrition.calculator.formula;

import java.util.function.Function;

@FunctionalInterface
public interface NutritionFormula<T extends NutritionFormulaArguments> extends
        Function<T, Integer> {

    @Override
    Integer apply(T t);

}
