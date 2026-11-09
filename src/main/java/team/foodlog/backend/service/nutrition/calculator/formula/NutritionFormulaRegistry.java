package team.foodlog.backend.service.nutrition.calculator.formula;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import team.foodlog.backend.service.nutrition.enums.NutritionFormulaType;

@Component
public class NutritionFormulaRegistry {

    private final Map<NutritionFormulaType, NutritionFormula> formulas;

    public NutritionFormulaRegistry(List<NutritionFormula> formulas) {
        this.formulas = formulas.stream()
                .collect(Collectors.toUnmodifiableMap(
                        NutritionFormula::getType,
                        Function.identity()
                ));
    }

    public NutritionFormula getRequired(NutritionFormulaType type) {
        NutritionFormula formula = formulas.get(type);

        if (formula == null) {
            throw new IllegalStateException("Unknown nutrition formula");
        }

        return formula;
    }

}
