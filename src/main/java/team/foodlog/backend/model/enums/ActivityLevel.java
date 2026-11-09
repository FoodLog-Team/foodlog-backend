package team.foodlog.backend.model.enums;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ActivityLevel {

    SEDENTARY(BigDecimal.valueOf(1.2)),

    LIGHTLY_ACTIVE(BigDecimal.valueOf(1.375)),

    MODERATELY_ACTIVE(BigDecimal.valueOf(1.55)),

    VERY_ACTIVE(BigDecimal.valueOf(1.725)),

    EXTRA_ACTIVE(BigDecimal.valueOf(1.9));

    private final BigDecimal multiplier;

}
