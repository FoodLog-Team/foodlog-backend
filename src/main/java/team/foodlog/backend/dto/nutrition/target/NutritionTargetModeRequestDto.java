package team.foodlog.backend.dto.nutrition.target;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import team.foodlog.backend.model.enums.TargetMode;

@Schema(
        description = """
                Request payload for switching the nutrition target mode between
                RECOMMENDED (auto-calculated based on user profile)
                and CUSTOM (manually set values).""",
        example = "{\"mode\": \"CUSTOM\"}"
)
public record NutritionTargetModeRequestDto(

        @Schema(
                description = """
                        The target mode to switch to.
                        RECOMMENDED uses values calculated from the user's nutrition profile.
                        CUSTOM uses manually specified values set via
                        the custom target endpoint.""",
                example = "CUSTOM",
                allowableValues = {"RECOMMENDED", "CUSTOM"},
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        TargetMode mode
) {
}
