package team.foodlog.backend.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import team.foodlog.backend.dto.auth.login.UserLoginRequestDto;
import team.foodlog.backend.dto.auth.register.UserRegistrationRequestDto;
import team.foodlog.backend.dto.auth.token.AccessTokenDto;
import team.foodlog.backend.dto.auth.token.TokenPairDto;
import team.foodlog.backend.security.AuthenticationService;
import team.foodlog.backend.security.RefreshTokenCookieFactory;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class JwtAuthenticationController implements AuthenticationController<AccessTokenDto> {

    private final AuthenticationService<TokenPairDto> authenticationService;

    private final RefreshTokenCookieFactory cookieFactory;

    @Override
    @PostMapping("/register")
    public ResponseEntity<AccessTokenDto> register(
            @Valid @RequestBody UserRegistrationRequestDto requestDto,
            HttpServletResponse response) {
        TokenPairDto tokenPair = authenticationService.register(requestDto);

        this.addCookieHeader(response, tokenPair.refreshToken());

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new AccessTokenDto(tokenPair.accessToken()));
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<AccessTokenDto> login(
            @Valid @RequestBody UserLoginRequestDto requestDto,
            HttpServletResponse response) {
        TokenPairDto tokenPair = authenticationService.login(requestDto);

        this.addCookieHeader(response, tokenPair.refreshToken());

        return ResponseEntity.ok(
                new AccessTokenDto(tokenPair.accessToken())
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenDto> refresh(
            @CookieValue(
                name = RefreshTokenCookieFactory.COOKIE_NAME,
                required = false
            ) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Refresh token is missing");
        }

        TokenPairDto tokenPair = authenticationService.refresh(refreshToken);

        this.addCookieHeader(response, tokenPair.refreshToken());

        return ResponseEntity.ok(
                new AccessTokenDto(tokenPair.accessToken())
        );
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(
                name = RefreshTokenCookieFactory.COOKIE_NAME,
                required = false
            ) String refreshToken,
            HttpServletResponse response
    ) {
        this.clearCookieHeader(response);

        if (refreshToken != null && !refreshToken.isBlank()) {
            authenticationService.logout(refreshToken);
        }

        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(HttpServletResponse response) {
        this.clearCookieHeader(response);

        authenticationService.logoutAll();

        return ResponseEntity.noContent().build();
    }

    private void addCookieHeader(HttpServletResponse response, String token) {
        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookieFactory.create(token).toString()
        );
    }

    private void clearCookieHeader(HttpServletResponse response) {
        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookieFactory.clear().toString()
        );
    }

}
