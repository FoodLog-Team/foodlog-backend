package team.foodlog.backend.security;

import team.foodlog.backend.dto.UserRegisterRequestDto;
import team.foodlog.backend.dto.UserResponseDto;

public interface AuthenticationService {

    UserResponseDto register(UserRegisterRequestDto requestDto);

}
