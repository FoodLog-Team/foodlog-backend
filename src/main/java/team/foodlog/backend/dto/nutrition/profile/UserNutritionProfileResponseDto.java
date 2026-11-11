package team.foodlog.backend.dto.nutrition.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import team.foodlog.backend.model.enums.ActivityLevel;
import team.foodlog.backend.model.enums.Sex;
import team.foodlog.backend.model.enums.WeightGoal;

@Schema(
        description = "Response containing the user's nutrition profile data. "
                + "Returned when creating or updating a nutrition profile."
)
public record UserNutritionProfileResponseDto(

        @Schema(
                description = "The unique identifier of the user.",
                example = "1"
        )
        Long userId,

        @Schema(
                description = "User's date of birth.",
                example = "1990-05-15",
                type = "string",
                format = "date"
        )
        LocalDate dateOfBirth,

        @Schema(
                description = "User's height in centimeters.",
                example = "175"
        )
        Integer height,

        @Schema(
                description = "User's weight in kilograms.",
                example = "70"
        )
        Integer weight,

        @Schema(
                description = "User's biological sex.",
                example = "MALE",
                allowableValues = {"MALE", "FEMALE"}
        )
        Sex sex,

        @Schema(
                description = "User's physical activity level.",
                example = "MODERATELY_ACTIVE",
                allowableValues = {
                        "SEDENTARY",
                        "LIGHTLY_ACTIVE",
                        "MODERATELY_ACTIVE",
                        "VERY_ACTIVE",
                        "EXTRA_ACTIVE"
                }
        )
        ActivityLevel activityLevel,

        @Schema(
                description = "User's weight goal.",
                example = "MAINTAIN_WEIGHT",
                allowableValues = {"LOSE_WEIGHT", "MAINTAIN_WEIGHT", "GAIN_WEIGHT"}
        )
        WeightGoal weightGoal
) {
}
