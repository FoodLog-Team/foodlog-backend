package team.foodlog.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import team.foodlog.backend.config.MapStructConfig;
import team.foodlog.backend.dto.nutrition.profile.CreateUserNutritionProfileRequestDto;
import team.foodlog.backend.dto.nutrition.profile.UpdateUserNutritionProfileRequestDto;
import team.foodlog.backend.dto.nutrition.profile.UserNutritionProfileDto;
import team.foodlog.backend.dto.nutrition.profile.UserNutritionProfileResponseDto;
import team.foodlog.backend.model.UserNutritionProfile;

@Mapper(config = MapStructConfig.class)
public interface UserNutritionProfileMapper {

    UserNutritionProfileResponseDto toResponseDto(UserNutritionProfile userNutritionProfile);

    UserNutritionProfileDto toDto(UserNutritionProfile userNutritionProfile);

    @Mapping(target = "userId", source = "userId")
    UserNutritionProfile toModel(CreateUserNutritionProfileRequestDto requestDto, Long userId);

    void update(UpdateUserNutritionProfileRequestDto requestDto,
                @MappingTarget UserNutritionProfile userNutritionProfile);

}
