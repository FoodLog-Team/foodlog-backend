package team.foodlog.backend.security;

import team.foodlog.backend.dto.UserRegistrationRequestDto;
import team.foodlog.backend.dto.UserRegistrationResponseDto;

public interface AuthenticationService {

    UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto);

}
