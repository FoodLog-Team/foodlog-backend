package team.foodlog.backend.service.nutrition.provider.usda;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record UsdaFood(
        @JsonProperty("fdcId") Long id,
        @JsonProperty("description") String name,
        @JsonProperty("foodNutrients") List<UsdaNutrient> nutrients
) {}
