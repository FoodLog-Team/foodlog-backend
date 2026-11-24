package team.foodlog.backend.service.nutrition.provider;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import org.springframework.stereotype.Component;
import team.foodlog.backend.exception.NoMatchingMealItemException;
import team.foodlog.backend.exception.NutritionInfoNotFoundException;
import team.foodlog.backend.service.nutrition.calculator.NutritionInfo;

@Component
public class NutritionInfoProviderChain {

    private final List<NutritionInfoProvider> providers;

    public NutritionInfoProviderChain(List<NutritionInfoProvider> providers) {
        this.providers = providers.stream()
                .sorted(Comparator.comparingInt(NutritionInfoProvider::getOrder))
                .toList();
    }

    public CompletableFuture<NutritionInfo> provide(String name) {
        return provide(name, 0);
    }

    private CompletableFuture<NutritionInfo> provide(String name, int index) {
        if (index >= providers.size()) {
            return CompletableFuture.failedFuture(
                    new NutritionInfoNotFoundException(
                            "No nutrition info found for '" + name + "'"
                    )
            );
        }

        return providers.get(index)
                .provide(name)
                .exceptionallyCompose(exception -> {
                    if (!isFallbackException(exception)) {
                        return CompletableFuture.failedFuture(exception);
                    }

                    return provide(name, index + 1);
                });
    }

    private boolean isFallbackException(Throwable exception) {
        Throwable cause = unwrapException(exception);

        return cause instanceof NutritionInfoNotFoundException
                || cause instanceof NoMatchingMealItemException;
    }

    private Throwable unwrapException(Throwable exception) {
        while ((exception instanceof CompletionException
                || exception instanceof ExecutionException)
                && exception.getCause() != null) {
            exception = exception.getCause();
        }

        return exception;
    }

}
