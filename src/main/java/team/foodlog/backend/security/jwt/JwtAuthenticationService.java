package team.foodlog.backend.security.jwt;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.foodlog.backend.dto.auth.login.UserLoginRequestDto;
import team.foodlog.backend.dto.auth.register.UserRegistrationRequestDto;
import team.foodlog.backend.dto.auth.token.RefreshTokenDto;
import team.foodlog.backend.dto.auth.token.TokenPairDto;
import team.foodlog.backend.exception.EmailAlreadyRegisteredException;
import team.foodlog.backend.model.User;
import team.foodlog.backend.repository.UserRepository;
import team.foodlog.backend.security.common.AuthenticatedUserIdSupplier;
import team.foodlog.backend.security.common.AuthenticationService;
import team.foodlog.backend.security.refresh.RefreshTokenService;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationService implements AuthenticationService<TokenPairDto> {

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticatedUserIdSupplier<Long> userIdSupplier;

    private final JwtService jwtService;

    private final RefreshTokenService<String, RefreshTokenDto> refreshTokenService;

    private final UserRepository userRepository;

    @Override
    @Transactional
    public TokenPairDto register(UserRegistrationRequestDto requestDto) {
        String email = normalizeEmail(requestDto.email());

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException(
                    "Account with this email is already registered");
        }

        User user = new User(
                email,
                passwordEncoder.encode(requestDto.password())
        );

        userRepository.saveAndFlush(user);

        Long userId = user.getId();
        return new TokenPairDto(
                jwtService.generateAccessToken(userId),
                refreshTokenService.create(userId).refreshToken()
        );
    }

    @Override
    @Transactional
    public TokenPairDto login(UserLoginRequestDto requestDto) {
        String email = normalizeEmail(requestDto.email());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email, requestDto.password()
                )
        );

        if (!(authentication.getPrincipal() instanceof UserDetails user)) {
            throw new IllegalStateException("Invalid authentication principal");
        }

        Long userId = userRepository.findByEmail(user.getUsername())
                .orElseThrow(
                        () -> new IllegalStateException(
                                "Authentication was not synced properly with database")
                ).getId();

        return new TokenPairDto(
                jwtService.generateAccessToken(userId),
                refreshTokenService.create(userId).refreshToken()
        );
    }

    @Override
    @Transactional
    public TokenPairDto refresh(String token) {
        RefreshTokenDto refreshTokenDto = refreshTokenService.rotate(token);

        return new TokenPairDto(
                jwtService.generateAccessToken(refreshTokenDto.userId()),
                refreshTokenDto.refreshToken()
        );
    }

    @Override
    public void logout(String token) {
        refreshTokenService.revoke(token);
    }

    @Override
    public void logoutAll() {
        refreshTokenService.revokeAll(userIdSupplier.get());
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
