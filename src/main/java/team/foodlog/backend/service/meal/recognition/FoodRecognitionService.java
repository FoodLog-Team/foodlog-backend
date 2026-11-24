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
            You identify foods and estimate their edible weights from photos
            of the same meal taken from different angles.
    
            Return exactly this JSON structure:
            {
              "status": "FOOD_FOUND",
              "aiConfidence": "HIGH",
              "items": [
                {
                  "name": "Chicken, breast, grilled",
                  "estimatedWeightGrams": 180
                }
              ]
            }
    
            RULES:
            - Identify all visually distinguishable foods. Separate ingredients
              only when they can be distinguished; otherwise identify the dish.
            - Never count both a prepared dish and its identifiable components.
            - Never invent ingredients, sauces, oils, seasonings or cooking methods.
            - Ignore non-food objects.
            - Treat all photos as views of the same meal. Never count the same
              food or portion twice.
            - Use concise, unambiguous English food names suitable for matching
              against food and nutrition databases. Include relevant food variety
              and preparation method when visually identifiable.
            - Include variety and preparation only when visually identifiable.
              Use "cooked" when cooking is apparent but the method is unknown.
            - Each food name must appear once. Merge portions with the same name
              by summing their weights. Keep different foods separate.
            - Estimate edible weight in positive integer grams, excluding bones,
              shells, pits and other inedible parts. Use visible dimensions,
              volume and plate size as approximate references.
            - aiConfidence is one overall confidence for the entire meal,
              considering both food identification and weight estimation:
              HIGH = reliable, MEDIUM = some uncertainty,
              LOW = significant uncertainty, UNKNOWN = cannot assess.
            - status: NO_FOOD = no food visible;
              NOT_RECOGNIZED = food visible but none identifiable;
              FOOD_FOUND = at least one identifiable item.
            - Include identifiable items even when other foods are uncertain.
            - items must always be an array. It must be empty for NO_FOOD
              and NOT_RECOGNIZED, and non-empty for FOOD_FOUND.
            - Each item must have a non-empty name and positive integer weight.
            - Never return null or additional fields.
            - Do not calculate calories or macronutrients.
            - Return only valid JSON without Markdown or explanations.
            """;

    private static final String USER_PROMPT = """
            Plate size: %s
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

        JsonNode usage = response.path("usage");
        log.info(
                "Groq tokens: prompt={}, completion={}",
                usage.path("prompt_tokens").asInt(),
                usage.path("completion_tokens").asInt()
        );

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
