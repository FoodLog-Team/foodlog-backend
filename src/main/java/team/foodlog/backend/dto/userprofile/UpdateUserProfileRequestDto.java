package team.foodlog.backend.dto.userprofile;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDate;
import team.foodlog.backend.dto.annotation.Adult;
import team.foodlog.backend.model.enums.ActivityLevel;
import team.foodlog.backend.model.enums.Sex;
import team.foodlog.backend.model.enums.WeightGoal;

public record UpdateUserProfileRequestDto(

        @Adult
        LocalDate dateOfBirth,

        @Min(120)
        @Max(230)
        Integer height,

        @DecimalMin("30.00")
        @DecimalMax("300.00")
        BigDecimal weight,

        Sex sex,

        ActivityLevel activityLevel,

        WeightGoal weightGoal

) {}
