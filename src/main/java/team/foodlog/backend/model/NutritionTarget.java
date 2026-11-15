package team.foodlog.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import team.foodlog.backend.model.enums.TargetMode;
import team.foodlog.backend.service.nutrition.enums.NutritionFormulaType;
import team.foodlog.backend.service.nutrition.enums.NutritionTargetCalculationType;

@Entity
@Accessors(chain = true)
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "nutrition_targets")
public class NutritionTarget {

    @Id
    @Column(name = "user_id")
    @NonNull
    @Setter(AccessLevel.NONE)
    private Long userId;

    @Column(nullable = false)
    private Integer kcal;

    @Column(nullable = false)
    private Integer carbs;

    @Column(nullable = false)
    private Integer protein;

    @Column(nullable = false)
    private Integer fats;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_mode", nullable = false, length = 32)
    private TargetMode targetMode = TargetMode.RECOMMENDED;

    @Enumerated(EnumType.STRING)
    @Column(name = "formula_type", nullable = false, length = 32)
    private NutritionFormulaType formulaType = NutritionFormulaType.MIFFLIN_ST_JEOR;

    @Enumerated(EnumType.STRING)
    @Column(name = "calculation_type", nullable = false, length = 32)
    private NutritionTargetCalculationType calculationType = NutritionTargetCalculationType.BASIC;

    @Version
    @Setter(AccessLevel.NONE)
    private Long version;

}
