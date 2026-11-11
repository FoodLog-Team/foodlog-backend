package team.foodlog.backend.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = """
        User's physical activity level.
        Used as a multiplier in calorie (TDEE) calculations.""")
@Getter
@RequiredArgsConstructor
public enum ActivityLevel {

    @Schema(description = "Little or no exercise, desk job. Multiplier: 1.2")
    SEDENTARY(BigDecimal.valueOf(1.2)),

    @Schema(description = "Light exercise or sports 1-3 days per week. Multiplier: 1.375")
    LIGHTLY_ACTIVE(BigDecimal.valueOf(1.375)),

    @Schema(description = "Moderate exercise or sports 3-5 days per week. Multiplier: 1.55")
    MODERATELY_ACTIVE(BigDecimal.valueOf(1.55)),

    @Schema(description = "Hard exercise or sports 6-7 days per week. Multiplier: 1.725")
    VERY_ACTIVE(BigDecimal.valueOf(1.725)),

    @Schema(description = """
            Very hard exercise, physical job, or training twice per day.
            Multiplier: 1.9""")
    EXTRA_ACTIVE(BigDecimal.valueOf(1.9));

    private final BigDecimal multiplier;

}
