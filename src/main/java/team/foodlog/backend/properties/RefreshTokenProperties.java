package team.foodlog.backend.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "security.refresh-token")
@Validated
public record RefreshTokenProperties(

        @NotNull
        Duration expiration,

        @Positive
        int maxAmount,

        @NotNull
        @Valid
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
            Duration maxAge
    ) {}

}
