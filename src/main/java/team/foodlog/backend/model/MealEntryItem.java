package team.foodlog.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import team.foodlog.backend.model.enums.MealItemFetchingStatus;

@Entity
@Accessors(chain = true)
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "meal_entry_items")
public class MealEntryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(name = "meal_entry_id", nullable = false)
    @NonNull
    @Setter(AccessLevel.NONE)
    private Long mealEntryId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 7, scale = 2)
    private BigDecimal grams;

    @Column(name = "kcal_per_100g", nullable = false, precision = 5, scale = 2)
    private BigDecimal kcalPer100g;

    @Column(name = "carbs_per_100g", nullable = false, precision = 5, scale = 2)
    private BigDecimal carbsPer100g;

    @Column(name = "protein_per_100g", nullable = false, precision = 5, scale = 2)
    private BigDecimal proteinPer100g;

    @Column(name = "fat_per_100g", nullable = false, precision = 5, scale = 2)
    private BigDecimal fatPer100g;

    @Enumerated(EnumType.STRING)
    @NonNull
    @Column(name = "status", nullable = false, length = 32)
    private MealItemFetchingStatus status = MealItemFetchingStatus.PENDING;

}
