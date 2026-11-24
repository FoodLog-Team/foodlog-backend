package team.foodlog.backend.service.nutrition.provider;

import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FoodMatcher {

    public static final int MINIMUM_SIMILARITY = 80;

    public Optional<Long> findBestMatch(String name, Map<Long, String> names) {
        String normalizedName = normalize(name);

        Optional<Long> exactMatch = names.entrySet().stream()
                .filter(entry -> normalize(entry.getValue()).equals(normalizedName))
                .map(Map.Entry::getKey)
                .findFirst();

        if (exactMatch.isPresent()) {
            log.debug("Exact match: {}", exactMatch.get());
            return exactMatch;
        }

        return names.entrySet().stream()
                .map(entry -> Map.entry(
                        entry.getKey(),
                        FuzzySearch.weightedRatio(
                                normalizedName,
                                normalize(entry.getValue())
                        )
                ))
                .filter(entry -> entry.getValue() >= MINIMUM_SIMILARITY)
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey);
    }

    private String normalize(String value) {
        return value.toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{L}\\p{N},\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

}
