package team.foodlog.backend.service.nutrition.provider.usda;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import team.foodlog.backend.config.AsyncConfig;
import team.foodlog.backend.config.ProviderConfig;
import team.foodlog.backend.exception.InvalidMealItemNameException;
import team.foodlog.backend.exception.NoMatchingMealItemException;
import team.foodlog.backend.exception.NutritionInfoNotFoundException;
import team.foodlog.backend.mapper.ToNutritionInfoMapper;
import team.foodlog.backend.properties.UsdaProperties;
import team.foodlog.backend.service.nutrition.calculator.NutritionInfo;
import team.foodlog.backend.service.nutrition.provider.FoodMatcher;
import team.foodlog.backend.service.nutrition.provider.FoodValidator;
import team.foodlog.backend.service.nutrition.provider.NutritionInfoProvider;
import team.foodlog.backend.service.nutrition.provider.NutritionInfoProviderType;

@Component
@RequiredArgsConstructor
@Slf4j
public class UsdaNutritionInfoProvider implements NutritionInfoProvider {

    private static final String PATH = "/foods/search";

    private final UsdaProperties usdaProperties;

    @Qualifier(ProviderConfig.USDA_REST_CLIENT_BEAN_NAME)
    private final RestClient restClient;

    private final FoodMatcher foodMatcher;

    private final FoodValidator<UsdaFood> foodValidator;

    private final ToNutritionInfoMapper<UsdaFood> mapper;

    @Override
    public NutritionInfoProviderType getType() {
        return NutritionInfoProviderType.USDA;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Async(AsyncConfig.USDA_REQUEST_EXECUTOR_BEAN_NAME)
    @Override
    public CompletableFuture<NutritionInfo> provide(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidMealItemNameException("Meal item name is blank or null");
        }

        UsdaFoodSearchResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(PATH)
                        .queryParam("query", name)
                        .queryParam("pageSize", 30)
                        .queryParam("api_key", usdaProperties.apiKey())
                        .build())
                .retrieve()
                .body(UsdaFoodSearchResponse.class);

        if (response == null || response.foods() == null || response.foods().isEmpty()) {
            throw new NutritionInfoNotFoundException(
                    "No nutrition info for '" + name + "' in USDA database");
        }

        log.debug("USDA returned {} foods for '{}'",
                response.foods().size(), name);

        List<UsdaFood> foods = response.foods().stream()
                .filter(foodValidator::isValid)
                .toList();

        log.debug("USDA valid foods for '{}': {}",
                name, foods.stream().map(UsdaFood::name).toList());

        if (foods.isEmpty()) {
            throw new NutritionInfoNotFoundException(
                    "No valid nutrition info for '" + name + "' in USDA database"
            );
        }

        Long bestMatchId = foodMatcher.findBestMatch(
                name, foods.stream().collect(Collectors.toUnmodifiableMap(
                        UsdaFood::id,
                        UsdaFood::name
                ))).orElseThrow(() -> new NoMatchingMealItemException(
                        "No matching food was found for '" + name + "'"
                )
        );

        UsdaFood bestMatch = foods.stream()
                .filter(food -> Objects.equals(food.id(), bestMatchId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Food matcher returned an ID that does not exist in the provided foods: "
                                + bestMatchId
                ));

        log.debug("Best match was: {}", bestMatch);

        return CompletableFuture.completedFuture(mapper.map(bestMatch));
    }

}
