package team.foodlog.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import team.foodlog.backend.config.MapStructConfig;
import team.foodlog.backend.dto.userprofile.CreateUserProfileRequestDto;
import team.foodlog.backend.dto.userprofile.UpdateUserProfileRequestDto;
import team.foodlog.backend.dto.userprofile.UserProfileResponseDto;
import team.foodlog.backend.model.UserProfile;

@Mapper(config = MapStructConfig.class)
public interface UserProfileMapper {

    UserProfileResponseDto toDto(UserProfile userProfile);

    @Mapping(target = "userId", source = "userId")
    UserProfile toModel(CreateUserProfileRequestDto requestDto, Long userId);

    void update(UpdateUserProfileRequestDto requestDto, @MappingTarget UserProfile userProfile);

}
