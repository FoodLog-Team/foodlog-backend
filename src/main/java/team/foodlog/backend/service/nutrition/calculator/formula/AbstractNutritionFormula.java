package team.foodlog.backend.service.nutrition.calculator.formula;

import team.foodlog.backend.dto.nutrition.profile.UserNutritionProfileDto;

public abstract class AbstractNutritionFormula<
        T extends NutritionFormulaArguments>
        implements NutritionFormula {

    @Override
    public final Integer calculate(UserNutritionProfileDto dto) {
        return apply(createArguments(dto));
    }

    protected abstract Integer apply(T arguments);

    protected abstract T createArguments(UserNutritionProfileDto dto);
}
