package team.foodlog.backend.service.nutrition.provider.usda;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import team.foodlog.backend.service.nutrition.provider.FoodValidator;

@Component
public class UsdaFoodValidator implements FoodValidator<UsdaFood> {

    private static final Set<Long> ENERGY_NUTRIENTS_IDS = Set.of(
            1008L, // kcal
            2048L, // Atwater Specific
            2047L, // Atwater General
            1062L // kJ
    );

    private static final Set<Long> REQUIRED_MACROS_IDS = Set.of(
            1005L, // carbs
            1003L, // protein
            1004L // fat
    );

    @Override
    public boolean isValid(UsdaFood food) {
        if (food.name() == null || food.name().isBlank()) {
            return false;
        }

        if (food.nutrients() == null) {
            return false;
        }

        Map<Long, BigDecimal> nutrients = food.nutrients().stream()
                .filter(nutrient -> nutrient.id() != null && nutrient.value() != null)
                .collect(Collectors.toMap(
                        UsdaNutrient::id,
                        UsdaNutrient::value,
                        (first, second) -> first
                ));

        return hasValidNutrients(nutrients);
    }

    private boolean hasValidNutrients(Map<Long, BigDecimal> nutrients) {
        return ENERGY_NUTRIENTS_IDS.stream()
                .anyMatch(id -> isValidNutrient(nutrients, id))
                && REQUIRED_MACROS_IDS.stream()
                .allMatch(id -> isValidNutrient(nutrients, id));
    }

    private boolean isValidNutrient(Map<Long, BigDecimal> nutrients, Long id) {
        BigDecimal value = nutrients.get(id);
        return value != null && value.signum() >= 0;
    }
}
