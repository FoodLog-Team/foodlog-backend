package team.foodlog.backend.dto.auth.token;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = """
                Internal DTO representing a refresh token tied to a specific user.
                Refresh tokens are opaque strings stored server-side and valid for 30 days.
                They are used to obtain new access tokens without re-authentication."""
)
public record RefreshTokenDto(

        @Schema(
                description = "The unique identifier of the user who owns this refresh token.",
                example = "1"
        )
        Long userId,

        @Schema(
                description = """
                        Opaque refresh token string. Valid for 30 days.
                        Stored in an HttpOnly cookie and used to rotate access tokens.""",
                example = "a1b2c3d4e5f6g7h8i9j0"
        )
        String refreshToken
) {
}
