package team.foodlog.backend.security;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import team.foodlog.backend.dto.UserRegistrationRequestDto;
import team.foodlog.backend.dto.UserRegistrationResponseDto;
import org.springframework.transaction.annotation.Transactional;
import team.foodlog.backend.exception.EmailAlreadyInUseException;
import team.foodlog.backend.model.User;
import team.foodlog.backend.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationService implements AuthenticationService {

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto) {
        String email = normalizeEmail(requestDto.email());

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyInUseException("Account with this email is already registered");
        }

        User user = new User();

        user.setEmail(email);

        user.setPassword(passwordEncoder.encode(requestDto.password()));

        userRepository.saveAndFlush(user);

        return new UserRegistrationResponseDto(
                jwtService.generateAccessToken(user.getId(), email),
                user.getId(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
