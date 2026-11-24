package team.foodlog.backend.service.nutrition.provider;

import java.util.concurrent.CompletableFuture;
import team.foodlog.backend.service.nutrition.calculator.NutritionInfo;

public interface NutritionInfoProvider {

    NutritionInfoProviderType getType();

    int getOrder();

    CompletableFuture<NutritionInfo> provide(String name);

}
