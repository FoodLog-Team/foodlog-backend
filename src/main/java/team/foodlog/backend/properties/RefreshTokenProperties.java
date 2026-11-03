package team.foodlog.backend.properties;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

@ConfigurationProperties(prefix = "security.refresh-token")
public record RefreshTokenProperties(
        @DurationUnit(ChronoUnit.DAYS)
        Duration expiration,

        int maxAmount,
        Cookies cookies
) {

    public record Cookies(
            String path,
            String sameSite,
            boolean secure,

            @DurationUnit(ChronoUnit.DAYS)
            Duration maxAge
    ) {}

}
