package team.foodlog.backend.mapper;

import org.mapstruct.Mapper;
import team.foodlog.backend.config.MapStructConfig;
import team.foodlog.backend.dto.UserRegistrationRequestDto;
import team.foodlog.backend.dto.UserRegistrationResponseDto;
import team.foodlog.backend.model.User;

@Mapper(config = MapStructConfig.class)
public interface UserMapper {

    User toModel(UserRegistrationRequestDto requestDto);

    UserRegistrationResponseDto toRegistrationResponseDto(User user);

}
