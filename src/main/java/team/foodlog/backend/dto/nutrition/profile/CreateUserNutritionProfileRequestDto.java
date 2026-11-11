package team.foodlog.backend.dto.nutrition.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import team.foodlog.backend.model.enums.ActivityLevel;
import team.foodlog.backend.model.enums.Sex;
import team.foodlog.backend.model.enums.WeightGoal;

@Schema(
        description = """
                Request payload for creating a new user nutrition profile.
                All fields are required. The profile is used to calculate personalized
                nutrition recommendations based on the user's
                physical characteristics and goals.""",
        example = """
                {"dateOfBirth": "1990-05-15",
                 "height": 175,
                 "weight": 70.5,
                 "sex": "MALE",
                 "activityLevel": "MODERATELY_ACTIVE",
                 "weightGoal": "MAINTAIN_WEIGHT"}"""
)
public record CreateUserNutritionProfileRequestDto(

        @Schema(
                description = "User's date of birth. The user must be at least 18 years old.",
                example = "1990-05-15",
                requiredMode = Schema.RequiredMode.REQUIRED,
                type = "string",
                format = "date"
        )
        @NotNull
        @Adult
        LocalDate dateOfBirth,

        @Schema(
                description = "User's height in centimeters. Must be between 120 and 230 cm.",
                example = "175",
                minimum = "120",
                maximum = "230",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        @Min(120)
        @Max(230)
        Integer height,

        @Schema(
                description = "User's weight in kilograms. Must be between 30 and 300 kg.",
                example = "70.5",
                minimum = "30",
                maximum = "300",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        @Min(30)
        @Max(300)
        BigDecimal weight,

        @Schema(
                description = "User's biological sex, used in nutrition formula calculations.",
                example = "MALE",
                allowableValues = {"MALE", "FEMALE"},
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        Sex sex,

        @Schema(
                description = """
                        User's physical activity level.
                        Used as a multiplier in calorie calculations.
                        SEDENTARY (1.2) = little or no exercise;
                        LIGHTLY_ACTIVE (1.375) = light exercise 1-3 days/week;
                        MODERATELY_ACTIVE (1.55) = moderate exercise 3-5 days/week;
                        VERY_ACTIVE (1.725) = hard exercise 6-7 days/week;
                        EXTRA_ACTIVE (1.9) = very hard exercise, physical job,
                        or training twice a day.""",
                example = "MODERATELY_ACTIVE",
                allowableValues = {
                        "SEDENTARY",
                        "LIGHTLY_ACTIVE",
                        "MODERATELY_ACTIVE",
                        "VERY_ACTIVE",
                        "EXTRA_ACTIVE"
                },
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        ActivityLevel activityLevel,

        @Schema(
                description = "User's weight goal. Affects the calorie target multiplier. "
                        + "LOSE_WEIGHT (0.85) = 15% calorie deficit; "
                        + "MAINTAIN_WEIGHT (1.0) = maintenance calories; "
                        + "GAIN_WEIGHT (1.15) = 15% calorie surplus.",
                example = "MAINTAIN_WEIGHT",
                allowableValues = {"LOSE_WEIGHT", "MAINTAIN_WEIGHT", "GAIN_WEIGHT"},
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull
        WeightGoal weightGoal

) {
}
