package team.foodlog.backend.service.nutrition;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.foodlog.backend.repository.NutritionTargetRepository;
import team.foodlog.backend.service.NutritionTargetService;
import team.foodlog.backend.service.nutrition.calculator.BasicNutritionTargetCalculationArguments;
import team.foodlog.backend.service.nutrition.calculator.NutritionTargetCalculator;
import team.foodlog.backend.service.nutrition.calculator.formula.BasicNutritionFormulaArguments;
import team.foodlog.backend.service.nutrition.calculator.formula.NutritionFormula;

@Service
public class BasicNutritionTargetService
        extends NutritionTargetService<BasicNutritionFormulaArguments, BasicNutritionTargetCalculationArguments> {

    private NutritionTargetRepository nutritionTargetRepository;

    private final NutritionFormula<BasicNutritionFormulaArguments> formula;

    private final NutritionTargetCalculator<BasicNutritionTargetCalculationArguments> calculator;

    public BasicNutritionTargetService(
            NutritionFormula<BasicNutritionFormulaArguments> formula,
            NutritionTargetCalculator<BasicNutritionTargetCalculationArguments> calculator) {
        super(formula, calculator);
    }

}
