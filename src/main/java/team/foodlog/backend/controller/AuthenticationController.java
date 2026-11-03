package team.foodlog.backend.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import team.foodlog.backend.dto.UserLoginRequestDto;
import team.foodlog.backend.dto.UserRegistrationRequestDto;

public interface AuthenticationController<T> {

    ResponseEntity<T> register(
            UserRegistrationRequestDto requestDto, HttpServletResponse response);

    ResponseEntity<T> login(UserLoginRequestDto requestDto, HttpServletResponse response);

    ResponseEntity<Void> logout(String value, HttpServletResponse response);

    ResponseEntity<Void> logoutAll(HttpServletResponse response);

}
