package team.foodlog.backend.dto.nutrition.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import team.foodlog.backend.model.enums.ActivityLevel;
import team.foodlog.backend.model.enums.Sex;
import team.foodlog.backend.model.enums.WeightGoal;

@Schema(
        description = """
                Internal DTO representing a user's nutrition profile data,
                including physical characteristics and fitness goals."""
)
public record UserNutritionProfileDto(

        @Schema(description = "User's date of birth",
                example = "1990-05-15", type = "string", format = "date")
        LocalDate dateOfBirth,

        @Schema(description = "User's height in centimeters", example = "175")
        Integer height,

        @Schema(description = "User's weight in kilograms", example = "70.5")
        BigDecimal weight,

        @Schema(description = "User's biological sex",
                example = "MALE", allowableValues = {"MALE", "FEMALE"})
        Sex sex,

        @Schema(description = "User's physical activity level", example = "MODERATELY_ACTIVE")
        ActivityLevel activityLevel,

        @Schema(description = "User's weight goal", example = "MAINTAIN_WEIGHT")
        WeightGoal weightGoal
) {}
