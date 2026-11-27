package team.foodlog.backend.service.nutrition.provider.usda;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;
import team.foodlog.backend.mapper.ToNutritionInfoMapper;
import team.foodlog.backend.service.nutrition.calculator.NutritionInfo;

@Component
public class UsdaToNutritionInfoMapper
            implements ToNutritionInfoMapper<UsdaFood> {

    private static final List<Integer> ENERGY_NUTRIENT_IDS = List.of(
            1008, // kcal
            2048, // Atwater Specific
            2047, // Atwater General
            1062 // kJ
    );

    private static final int CARBS_NUTRIENT_ID = 1005;

    private static final int PROTEIN_NUTRIENT_ID = 1003;

    private static final int FAT_NUTRIENT_ID = 1004;

    @Override
    public NutritionInfo map(UsdaFood from) {
        List<UsdaNutrient> nutrients = from.nutrients();

        return new NutritionInfo(
                getKcal(nutrients),
                getValueByNutritionId(nutrients, CARBS_NUTRIENT_ID),
                getValueByNutritionId(nutrients, PROTEIN_NUTRIENT_ID),
                getValueByNutritionId(nutrients, FAT_NUTRIENT_ID)
        );
    }

    private BigDecimal getKcal(List<UsdaNutrient> nutrients) {
        for (int id : ENERGY_NUTRIENT_IDS) {
            for (UsdaNutrient nutrient : nutrients) {
                if (nutrient.id() == id && nutrient.value() != null) {
                    BigDecimal value = nutrient.value();

                    return id == 1062
                            ? value.divide(BigDecimal.valueOf(4.184), 2, RoundingMode.HALF_UP)
                            : value;
                }
            }
        }

        throw new IllegalStateException("Validated USDA food is missing kcal nutrient");
    }

    private BigDecimal getValueByNutritionId(List<UsdaNutrient> nutrients, int id) {
        return nutrients.stream()
                .filter(nutrient -> nutrient.id() == id)
                .map(UsdaNutrient::value)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Validated USDA food is missing required nutrient ID: " + id
                ));
    }
}
