package team.foodlog.backend.security;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;
import team.foodlog.backend.properties.JwtProperties;

@Component
@RequiredArgsConstructor
public class NimbusJwtService implements JwtService {

    private static final String TOKEN_ISSUER = "foodlog";

    private final JwtEncoder jwtEncoder;

    private final JwtProperties jwtProperties;

    @Override
    public String generateAccessToken(Long userId, String email) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(TOKEN_ISSUER)
                .subject(userId.toString())
                .issuedAt(now)
                .expiresAt(now.plusMillis(jwtProperties.expiration()))
                .claim("email", email)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }

}
