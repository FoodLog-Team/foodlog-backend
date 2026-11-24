package team.foodlog.backend.dto.meal;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import team.foodlog.backend.model.enums.MealItemFetchingStatus;

@Schema(
        description = """
                Food item creation response model. The current POST /meals/products endpoint
                does not return this DTO; it responds with HTTP 202 and no body.
                """
)
public record MealEntryItemCreatedResponseDto(

        @Schema(description = "ID of the parent meal entry.", example = "1")
        Long mealEntryId,

        @Schema(
                description = "Initial nutrition lookup status of the food item.",
                example = "PENDING"
        )
        MealItemFetchingStatus status,

        @Schema(
                description = "Timestamp when the food item was created.",
                example = "2026-11-24T18:30:00Z",
                type = "string",
                format = "date-time"
        )
        Instant createdAt

) {}
