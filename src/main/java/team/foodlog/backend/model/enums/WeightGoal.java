package team.foodlog.backend.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(description = """
        User's weight goal.
        Determines the calorie target multiplier applied to the calculated TDEE.""")
@Getter
@RequiredArgsConstructor
public enum WeightGoal {

    @Schema(description = "Lose weight — applies a 15% calorie deficit (multiplier: 0.85)")
    LOSE_WEIGHT(BigDecimal.valueOf(0.85)),

    @Schema(description = "Maintain current weight — no adjustment (multiplier: 1.0)")
    MAINTAIN_WEIGHT(BigDecimal.ONE),

    @Schema(description = "Gain weight — applies a 15% calorie surplus (multiplier: 1.15)")
    GAIN_WEIGHT(BigDecimal.valueOf(1.15));

    private final BigDecimal multiplier;
}
