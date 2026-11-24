package team.foodlog.backend.service.meal.recognition;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import team.foodlog.backend.model.enums.AiConfidence;
import team.foodlog.backend.service.meal.recognition.enums.RecognitionStatus;

public record FoodRecognitionResult(

        @NotNull
        RecognitionStatus status,

        @NotNull
        AiConfidence aiConfidence,

        @NotNull
        List<@Valid RecognizedFoodItem> items

) {}
