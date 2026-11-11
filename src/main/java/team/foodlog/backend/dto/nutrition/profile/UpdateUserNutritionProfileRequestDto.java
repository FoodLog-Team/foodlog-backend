package team.foodlog.backend.dto.nutrition.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDate;
import team.foodlog.backend.model.enums.ActivityLevel;
import team.foodlog.backend.model.enums.Sex;
import team.foodlog.backend.model.enums.WeightGoal;

@Schema(
        description = """
                Request payload for updating an existing user nutrition profile.
                All fields are optional — only the fields included in the request will be updated.
                Omitted fields retain their current values.""",
        example = "{\"weight\": 68.0, \"activityLevel\": \"VERY_ACTIVE\"}"
)
public record UpdateUserNutritionProfileRequestDto(

        @Schema(
                description = "User's date of birth. The user must be at least 18 years old. "
                        + "Omit this field to keep the current value.",
                example = "1990-05-15",
                type = "string",
                format = "date"
        )
        @Adult
        LocalDate dateOfBirth,

        @Schema(
                description = "User's height in centimeters. Must be between 120 and 230 cm. "
                        + "Omit this field to keep the current value.",
                example = "175",
                minimum = "120",
                maximum = "230"
        )
        @Min(120)
        @Max(230)
        Integer height,

        @Schema(
                description = "User's weight in kilograms. Must be between 30 and 300 kg. "
                        + "Omit this field to keep the current value.",
                example = "68.0",
                minimum = "30",
                maximum = "300"
        )
        @DecimalMin("30.00")
        @DecimalMax("300.00")
        BigDecimal weight,

        @Schema(
                description = "User's biological sex. Omit this field to keep the current value.",
                example = "MALE",
                allowableValues = {"MALE", "FEMALE"}
        )
        Sex sex,

        @Schema(
                description = """
                        User's physical activity level.
                        Omit this field to keep the current value.""",
                example = "VERY_ACTIVE",
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
                description = "User's weight goal. Omit this field to keep the current value.",
                example = "LOSE_WEIGHT",
                allowableValues = {"LOSE_WEIGHT", "MAINTAIN_WEIGHT", "GAIN_WEIGHT"}
        )
        WeightGoal weightGoal

) {
}
