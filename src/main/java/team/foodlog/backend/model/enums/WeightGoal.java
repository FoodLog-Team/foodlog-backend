package team.foodlog.backend.model.enums;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WeightGoal {

    LOSE_WEIGHT(BigDecimal.valueOf(0.85)),

    MAINTAIN_WEIGHT(BigDecimal.ONE),

    GAIN_WEIGHT(BigDecimal.valueOf(1.15));

    private final BigDecimal multiplier;

}
