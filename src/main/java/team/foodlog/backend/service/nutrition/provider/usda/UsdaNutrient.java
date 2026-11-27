package team.foodlog.backend.service.nutrition.provider.usda;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record UsdaNutrient(
        @JsonProperty("nutrientId") Long id,
        BigDecimal value
) {}
