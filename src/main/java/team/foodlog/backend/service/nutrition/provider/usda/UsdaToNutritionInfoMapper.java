package team.foodlog.backend.service.nutrition.provider.usda;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;
import team.foodlog.backend.mapper.ToNutritionInfoMapper;
import team.foodlog.backend.service.nutrition.calculator.NutritionInfo;

@Component
public class UsdaToNutritionInfoMapper
            implements ToNutritionInfoMapper<UsdaFood> {

    private static final String KCAL_NUTRIENT_NAME = "Energy";

    private static final String CARBS_NUTRIENT_NAME = "Carbohydrate, by difference";

    private static final String PROTEIN_NUTRIENT_NAME = "Protein";

    private static final String FAT_NUTRIENT_NAME = "Total lipid (fat)";

    @Override
    public NutritionInfo map(UsdaFood from) {
        List<UsdaNutrient> nutrients = from.nutrients();

        return new NutritionInfo(
                getValueByNutritionName(nutrients, KCAL_NUTRIENT_NAME),
                getValueByNutritionName(nutrients, CARBS_NUTRIENT_NAME),
                getValueByNutritionName(nutrients, PROTEIN_NUTRIENT_NAME),
                getValueByNutritionName(nutrients, FAT_NUTRIENT_NAME)
        );
    }

    private BigDecimal getValueByNutritionName(List<UsdaNutrient> nutrients, String name) {
        return nutrients.stream()
                .filter(nutrient -> Objects.equals(nutrient.name(), name))
                .map(UsdaNutrient::value)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Validated USDA food is missing required nutrient: " + name
                ));
    }
}
