package team.foodlog.backend.security;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import team.foodlog.backend.properties.RefreshTokenProperties;

@Component
@RequiredArgsConstructor
public class RefreshTokenCookieFactory {

    public static final String COOKIE_NAME = "refresh_token_cookie";

    private final RefreshTokenProperties properties;

    public ResponseCookie create(String refreshToken) {
        return build(refreshToken, properties.cookies().maxAge());
    }

    public ResponseCookie clear() {
        return build("", Duration.ZERO);
    }

    private ResponseCookie build(String token, Duration maxAge) {
        return ResponseCookie.from(COOKIE_NAME, token)
                .httpOnly(true)
                .secure(properties.cookies().secure())
                .sameSite(properties.cookies().sameSite())
                .path(properties.cookies().path())
                .maxAge(maxAge)
                .build();
    }

}
