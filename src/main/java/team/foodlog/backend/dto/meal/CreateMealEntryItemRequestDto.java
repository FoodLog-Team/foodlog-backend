package team.foodlog.backend.dto.meal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Schema(
        name = "CreateMealEntryItemRequest",
        description = """
                Request payload for manually adding a food item to an existing meal entry.
                """,
        example = """
                {
                    "name": "Bread",
                    "weight": 80.0
                }
                """
)
public record CreateMealEntryItemRequestDto(

        @Schema(
                description = """
                        Food name used to search the nutrition database.
                        English food names are recommended.
                        """,
                example = "Bread",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank
        String name,

        @Schema(
                description = "Estimated food weight in grams. Must be greater than zero.",
                example = "80.0",
                minimum = "0",
                exclusiveMinimum = true,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        @Positive
        BigDecimal weight

) {}
