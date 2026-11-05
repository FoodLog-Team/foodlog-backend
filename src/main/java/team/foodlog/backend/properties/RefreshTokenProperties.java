package team.foodlog.backend.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "security.refresh-token")
@Validated
public record RefreshTokenProperties(

        @NotNull
        @DurationUnit(ChronoUnit.DAYS)
        Duration expiration,

        @Positive
        int maxAmount,

        Cookies cookies
) {

    public record Cookies(

            @NotBlank
            String path,

            @NotBlank
            String sameSite,

            @NotNull
            Boolean secure,

            @NotNull
            @DurationUnit(ChronoUnit.DAYS)
            Duration maxAge
    ) {}

}
