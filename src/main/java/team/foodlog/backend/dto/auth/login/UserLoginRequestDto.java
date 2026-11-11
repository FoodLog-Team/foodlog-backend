package team.foodlog.backend.dto.auth.login;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@Schema(
        description = "Request payload for user login authentication",
        example = "{\"email\": \"user@example.com\", \"password\": \"SecureP@ss1\"}"
)
public record UserLoginRequestDto(

        @Schema(
                description = "User's registered email address. Must be a valid email format.",
                example = "user@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank
        @Email
        String email,

        @Schema(
                description = """
                        User's password. Must be between 8 and 32 characters.
                        Must contain at least one uppercase letter, one lowercase letter,
                        one digit, and one special character.""",
                example = "SecureP@ss1",
                minLength = 8,
                maxLength = 32,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank
        @Length(min = 8, max = 32)
        String password
) {
}
