package team.foodlog.backend.dto.userprofile;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import team.foodlog.backend.model.enums.ActivityLevel;
import team.foodlog.backend.model.enums.Sex;
import team.foodlog.backend.model.enums.WeightGoal;

public record UpdateUserProfileRequestDto(

        @Min(18)
        @Max(100)
        Integer age,

        @Min(120)
        @Max(230)
        Integer height,

        @Min(30)
        @Max(300)
        Integer weight,

        Sex sex,

        ActivityLevel activityLevel,

        WeightGoal weightGoal

) {}
