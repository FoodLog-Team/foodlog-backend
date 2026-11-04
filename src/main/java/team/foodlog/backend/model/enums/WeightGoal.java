package team.foodlog.backend.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WeightGoal {

    LOSE_WEIGHT(0.85),

    MAINTAIN_WEIGHT(1),

    GAIN_WEIGHT(1.15);

    private final double calorieMultiplier;

}
