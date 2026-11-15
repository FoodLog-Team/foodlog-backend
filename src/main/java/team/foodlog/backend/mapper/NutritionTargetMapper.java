package team.foodlog.backend.mapper;

import org.mapstruct.Mapper;
import team.foodlog.backend.config.MapStructConfig;
import team.foodlog.backend.dto.nutrition.target.NutritionTargetResponseDto;
import team.foodlog.backend.model.NutritionTarget;

@Mapper(config = MapStructConfig.class)
public interface NutritionTargetMapper {

    NutritionTargetResponseDto toDto(NutritionTarget model);

}
