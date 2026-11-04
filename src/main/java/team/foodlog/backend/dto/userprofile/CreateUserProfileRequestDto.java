package team.foodlog.backend.dto.userprofile;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import team.foodlog.backend.model.enums.ActivityLevel;
import team.foodlog.backend.model.enums.Sex;
import team.foodlog.backend.model.enums.WeightGoal;

public record CreateUserProfileRequestDto(

            @NotNull
            @Min(18)
            @Max(100)
            Integer age,

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
