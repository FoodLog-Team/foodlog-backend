package team.foodlog.backend.mapper;

import org.mapstruct.Mapper;
import team.foodlog.backend.config.MapStructConfig;
import team.foodlog.backend.dto.UserRegisterRequestDto;
import team.foodlog.backend.dto.UserResponseDto;
import team.foodlog.backend.model.User;

@Mapper(config = MapStructConfig.class)
public interface UserMapper {

    User toModel(UserRegisterRequestDto requestDto);

    UserResponseDto toDto(User user);

}
