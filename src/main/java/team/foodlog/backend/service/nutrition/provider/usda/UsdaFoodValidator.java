package team.foodlog.backend.service.nutrition.provider.usda;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import team.foodlog.backend.service.nutrition.provider.FoodValidator;

@Component
public class UsdaFoodValidator implements FoodValidator<UsdaFood> {

    private static final Set<String> REQUIRED_NUTRIENTS = Set.of(
            "Energy",
            "Carbohydrate, by difference",
            "Protein",
            "Total lipid (fat)"
    );

    @Override
    public boolean isValid(UsdaFood food) {
        if (food.name() == null || food.name().isBlank()) {
            return false;
        }

        if (food.nutrients() == null) {
            return false;
        }

        Map<String, BigDecimal> nutrients = food.nutrients().stream()
                .filter(nutrient -> nutrient.name() != null && nutrient.value() != null)
                .collect(Collectors.toMap(
                        UsdaNutrient::name,
                        UsdaNutrient::value,
                        (first, second) -> first
                ));

        return REQUIRED_NUTRIENTS.stream()
                .allMatch(name -> {
                    BigDecimal value = nutrients.get(name);
                    return value != null && value.signum() >= 0;
                });
    }
}
