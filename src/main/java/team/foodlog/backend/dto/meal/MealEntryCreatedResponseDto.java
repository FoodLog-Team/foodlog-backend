package team.foodlog.backend.dto.meal;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import team.foodlog.backend.model.enums.MealAnalysisStatus;

@Schema(
        description = """
                Response returned after a meal entry has been created and asynchronous
                food recognition has been accepted.
                """
)
public record MealEntryCreatedResponseDto(

        @Schema(description = "ID of the created meal entry.", example = "1")
        Long id,

        @Schema(
                description = "Initial processing status of the meal entry.",
                example = "PENDING"
        )
        MealAnalysisStatus analysisStatus,

        @Schema(
                description = "Timestamp when the meal entry was created.",
                example = "2026-11-24T18:30:00Z",
                type = "string",
                format = "date-time"
        )
        Instant createdAt

) {}
