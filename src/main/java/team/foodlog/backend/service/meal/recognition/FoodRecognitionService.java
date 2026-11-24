package team.foodlog.backend.service.meal.recognition;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import team.foodlog.backend.config.AiConfig;
import team.foodlog.backend.model.MealEntryPhoto;
import team.foodlog.backend.model.enums.PlateSize;
import team.foodlog.backend.properties.GroqProperties;
import team.foodlog.backend.service.meal.recognition.enums.RecognitionStatus;
import team.foodlog.backend.service.photo.PhotoStorage;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class FoodRecognitionService {

    private static final String SYSTEM_PROMPT = """
                You are a visual food recognition and portion estimation system.
        
                Your task:
                1. Identify distinct, visually identifiable food items.
                2. Estimate the edible weight of each item in grams.
                3. Assess identification and weight confidence independently.
        
                IMAGE ANALYSIS:
                - All images show the same meal from different angles.
                - Combine information from all images.
                - Never count the same food or portion more than once.
                - Use all views to improve identification and weight estimation.
        
                FOOD IDENTIFICATION:
                - Identify distinguishable ingredients separately,
                  especially in salads, bowls and mixed dishes.
                - Never combine identifiable ingredients into a generic
                  dish or count both a dish and its components.
                - If ingredients cannot be reliably distinguished,
                  return the recognizable prepared dish as one item.
                - Include only visually identifiable food. Never invent ingredients,
                  sauces, oils, seasonings or cooking methods.
                - Ignore plates, utensils, packaging and other non-food objects.
        
                FOOD NAMING:
                - Use concise, standardized English food names suitable
                  for searching nutritional databases.
                - Put the main food name first, followed by relevant variety, cut and preparation.
                - Use names such as:
                  - "Rice, white, cooked"
                  - "Chicken, breast, roasted"
                  - "Tomato, raw"
                  - "Cucumber, raw"
                  - "Pasta, cooked"
                - Include preparation methods and varieties only when visually identifiable.
                - If clearly cooked but the method is uncertain, use "cooked".
                - Avoid descriptions, subjective commentary and unnecessary details.
        
                WEIGHT ESTIMATION:
                - Return positive integer weights in grams.
                - Estimate edible portions only; exclude bones, shells,
                  pits and other inedible parts.
                - Consider visible dimensions, volume, thickness and plate size.
                - Treat plate size as an approximate scale reference, not an exact measurement.
                - Estimate each distinguishable component independently.
                - Reduce weight confidence when portions are obscured or difficult to estimate.
        
                CONFIDENCE:
                - Identification confidence measures certainty about food identity.
                - Weight confidence measures reliability of the estimated weight.
                - Assess them independently.
                - Do not assign high confidence to uncertain results.
        
                CLASSIFICATION:
                - NO_FOOD: no food is visible.
                - NOT_RECOGNIZED: food is visible, but no item can be identified.
                - FOOD_FOUND: at least one item is identified.
                - If only some items are identifiable, return FOOD_FOUND with only those items.
        
                    FEW-SHOT EXAMPLES:
            
                             Example 1:
                             Input: A plate with salmon fillet, mashed potatoes,
                              broccoli and carrots.
                             Output food items:
                             - Salmon, cooked
                             - Potatoes, mashed
                             - Broccoli, cooked
                             - Carrots, cooked
            
                             Example 2:
                             Input: A bowl with visibly distinguishable spinach,
                                    avocado and boiled eggs.
                             Output food items:
                             - Spinach, raw
                             - Avocado, raw
                             - Egg, whole, hard-boiled
            
                             Do not return "Spinach salad with avocado and eggs".
            
                             Example 3:
                             Input: A serving of mushroom risotto with no individually
                                    distinguishable ingredients.
                             Output food items:
                             - Risotto, mushroom
            
                JSON RESPONSE STRUCTURE:
            
                Return a valid JSON object with exactly the following structure:
            
                {
                  "status": "FOOD_FOUND",
                  "aiConfidence": "HIGH",
                  "items": [
                    {
                      "name": "Chicken, breast, grilled",
                      "estimatedWeightGrams": 180
                    },
                    {
                      "name": "Rice, white, cooked",
                      "estimatedWeightGrams": 160
                    }
                  ]
                }
            
                REQUIREMENTS:
                - Use exactly these field names. Do not rename or omit any fields.
                - "status" must be one of:
                  - "NO_FOOD"
                  - "NOT_RECOGNIZED"
                  - "FOOD_FOUND"
                - "aiConfidence" must be one of:
                  - "LOW"
                  - "MEDIUM"
                  - "HIGH"
                  - "UNKNOWN"
                - "items" must always be an array.
                - Each item must contain:
                  - "name": non-empty string containing a standardized English food name.
                  - "estimatedWeightGrams": positive number representing edible weight in grams.
                - Never return null for any field.
                - For "NO_FOOD" and "NOT_RECOGNIZED", return an empty "items" array.
                - For "FOOD_FOUND", return at least one identified food item.
                - Return only valid JSON. Do not include Markdown, explanations or text
                  outside the JSON object.
        
                GENERAL:
                - Base every conclusion exclusively on visual evidence.
                - Do not calculate calories or macronutrients.
                - Return only the requested structured result.
                - Never return same food name twice.
            
                OUTPUT FORMAT:
                - Return a valid JSON object.
                - The response must follow the requested JSON structure.
                - Do not include Markdown or text outside the JSON object.
            """;

    private static final String USER_PROMPT = """
                Analyze the attached photographs of the same meal.
        
                Plate size: %s
        
                Identify all distinct, visually identifiable food items and
                estimate their edible weights in grams.
        
                Use standardized English food names suitable for searching nutritional databases.
        
                Return identification and weight confidence independently.
            """;

    private final GroqProperties groqProperties;

    @Qualifier(AiConfig.GROQ_REST_CLIENT_BEAN_NAME)
    private final RestClient groqRestClient;

    private final JsonMapper jsonMapper;

    private final Validator validator;

    private final PhotoStorage photoStorage;

    public FoodRecognitionResult recognize(
            List<MealEntryPhoto> photos, PlateSize plateSize
    ) {
        if (photos.isEmpty() || photos.size() > 2) {
            throw new IllegalArgumentException(
                    "At least 1 photo must be sent. Maximum 2"
            );
        }

        List<Map<String, Object>> content = new ArrayList<>();

        content.add(Map.of(
                "type", "text",
                "text", SYSTEM_PROMPT + "\n\n"
                        + USER_PROMPT.formatted(plateSize)
        ));

        photos.forEach(photo -> content.add(toMedia(photo)));

        Map<String, Object> request = Map.of(
                "model", groqProperties.model(),
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", content
                        )
                ),
                "temperature", 1.0,
                "top_p", 0.95,
                "max_completion_tokens", 1500,
                "reasoning_effort", "low",
                "reasoning_format", "hidden",
                "response_format", Map.of(
                        "type", "json_object"
                )
        );

        JsonNode response = groqRestClient.post()
                .uri("/chat/completions")
                .body(request)
                .retrieve()
                .body(JsonNode.class);

        String json = Objects.requireNonNull(response)
                .path("choices")
                .path(0)
                .path("message")
                .path("content")
                .asString();

        log.info("Groq response: {}", json);

        try {
            FoodRecognitionResult result = jsonMapper.readValue(
                    json,
                    FoodRecognitionResult.class
            );

            var violations = validator.validate(result);

            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }

            if (result.status() == RecognitionStatus.FOOD_FOUND
                    && result.items().isEmpty()) {
                throw new IllegalStateException(
                        "AI returned FOOD_FOUND with an empty items list"
                );
            }

            if (result.status() != RecognitionStatus.FOOD_FOUND
                    && !result.items().isEmpty()) {
                throw new IllegalStateException(
                        "AI returned food items with a non-FOOD_FOUND status"
                );
            }

            return result;
        } catch (JacksonException e) {
            throw new IllegalStateException(
                    "Failed to parse AI response",
                    e
            );
        }
    }

    private Map<String, Object> toMedia(MealEntryPhoto photo) {
        try {
            byte[] bytes = photoStorage.get(photo.getStorageKey())
                    .getContentAsByteArray();

            String base64 = Base64.getEncoder()
                    .encodeToString(bytes);

            return Map.of(
                    "type", "image_url",
                    "image_url", Map.of(
                            "url", "data:"
                                    + photo.getFormat().getContentType()
                                    + ";base64,"
                                    + base64
                    )
            );
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
