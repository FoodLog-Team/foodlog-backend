package team.foodlog.backend.security;

import team.foodlog.backend.dto.auth.login.UserLoginRequestDto;
import team.foodlog.backend.dto.auth.register.UserRegistrationRequestDto;

public interface AuthenticationService<T> {

    T register(UserRegistrationRequestDto requestDto);

    T login(UserLoginRequestDto requestDto);

    T refresh(String token);

    void logout(String token);

    void logoutAll();

}
