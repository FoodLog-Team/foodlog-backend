package team.foodlog.backend.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "groq")
@Validated
public record GroqProperties(

        @NotBlank
        String apiKey,

        @NotBlank
        String baseUrl,

        @NotBlank
        String model

) {}
