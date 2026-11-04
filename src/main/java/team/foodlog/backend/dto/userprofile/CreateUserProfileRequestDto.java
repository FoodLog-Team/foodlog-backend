package team.foodlog.backend.dto.userprofile;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import team.foodlog.backend.dto.annotation.Adult;
import team.foodlog.backend.model.enums.ActivityLevel;
import team.foodlog.backend.model.enums.Sex;
import team.foodlog.backend.model.enums.WeightGoal;

public record CreateUserProfileRequestDto(

            @NotNull
            @Adult
            LocalDate age,

            @NotNull
            @Min(120)
            @Max(230)
            Integer height,

            @NotNull
            @Min(30)
            @Max(300)
            Integer weight,

            @NotNull
            Sex sex,

            @NotNull
            ActivityLevel activityLevel,

            @NotNull
            WeightGoal weightGoal

) {}
