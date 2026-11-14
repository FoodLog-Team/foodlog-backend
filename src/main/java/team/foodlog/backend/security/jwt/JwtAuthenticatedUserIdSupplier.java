package team.foodlog.backend.security.jwt;

import java.util.Objects;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import team.foodlog.backend.security.common.AuthenticatedUserIdSupplier;

@Component
public class JwtAuthenticatedUserIdSupplier implements AuthenticatedUserIdSupplier {

    public Long get() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException(
                    "Authentication is empty, make sure to use this"
                        + " method withing authenticated endpoints");
        }

        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            throw new IllegalStateException(
                    "Authentication class is: " + authentication.getClass().getSimpleName()
                    + ". Must be JwtAuthenticationToken"
            );
        }

        Jwt principal = jwtAuth.getToken();
        Objects.requireNonNull(principal, "Authentication principal is null");

        String userId = principal.getSubject();
        Objects.requireNonNull(userId, "User id is null");

        try {
            return Long.valueOf(userId);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("User id has invalid Long format");
        }

    }

}
