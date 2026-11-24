package team.foodlog.backend.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "usda")
@Validated
public record UsdaProperties(

        @NotBlank
        String baseUrl,

        @NotBlank
        String apiKey

) {}
