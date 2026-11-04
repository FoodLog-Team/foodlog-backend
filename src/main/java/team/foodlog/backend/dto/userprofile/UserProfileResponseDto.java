package team.foodlog.backend.dto.userprofile;

import team.foodlog.backend.model.enums.ActivityLevel;
import team.foodlog.backend.model.enums.Sex;
import team.foodlog.backend.model.enums.WeightGoal;

public record UserProfileResponseDto(
        Long id,
        Integer height,
        Integer weight,
        Sex sex,
        ActivityLevel activityLevel,
        WeightGoal weightGoal
) {}
