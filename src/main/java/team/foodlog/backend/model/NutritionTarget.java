package team.foodlog.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import team.foodlog.backend.model.enums.TargetMode;

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

    @Column(name = "basal_metabolic_rate", nullable = false)
    private Integer basalMetabolicRate;

    @Column(name = "recommended_kcal", nullable = false)
    private Integer recommendedKcal;

    @Column(name = "recommended_carbs", nullable = false)
    private Integer recommendedCarbs;

    @Column(name = "recommended_protein", nullable = false)
    private Integer recommendedProtein;

    @Column(name = "recommended_fats", nullable = false)
    private Integer recommendedFats;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false, length = 32)
    private TargetMode mode;

    private Integer customCalories;

    private Integer customCarbs;

    private Integer customProtein;

    private Integer customFats;

}
