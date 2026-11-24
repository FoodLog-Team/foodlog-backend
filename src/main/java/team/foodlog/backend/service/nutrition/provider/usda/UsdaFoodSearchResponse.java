package team.foodlog.backend.service.nutrition.provider.usda;

import java.util.List;

public record UsdaFoodSearchResponse(
        List<UsdaFood> foods
) {}
