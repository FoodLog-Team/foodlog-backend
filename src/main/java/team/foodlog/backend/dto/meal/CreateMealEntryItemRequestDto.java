package team.foodlog.backend.dto.meal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CreateMealEntryItemRequestDto(

        @NotNull
        Long mealEntryId,

        @NotBlank
        String name,

        @Positive
        BigDecimal weight

) {}
