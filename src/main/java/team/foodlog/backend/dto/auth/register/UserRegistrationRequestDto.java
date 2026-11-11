package team.foodlog.backend.dto.auth.register;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import team.foodlog.backend.dto.auth.login.StrongPassword;

@Schema(
        description = "Request payload for registering a new user account",
        example = "{\"email\": \"newuser@example.com\", \"password\": \"SecureP@ss1\"}"
)
public record UserRegistrationRequestDto(

        @Schema(
                description = "User's email address. Must be a valid and unique email format.",
                example = "newuser@example.com",
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
        @StrongPassword
        @Length(min = 8, max = 32)
        String password
) {
}
