package team.foodlog.backend.dto.auth.token;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = """
                Internal DTO containing both an access token and a refresh token. 
                Used internally after successful authentication to return the token pair. 
                The access token is returned in the response body, while the refresh token 
                is set as an HttpOnly cookie."""
)
public record TokenPairDto(

        @Schema(
                description = "JWT access token string. Valid for 15 minutes.",
                example = """
                        eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.doz
                        jgNryP4J3jVmNHl0w5N_XgL0n3I9PlFUP0THsR8U"""
        )
        String accessToken,

        @Schema(
                description = """
                        Opaque refresh token string. Valid for 30 days. 
                        Stored in an HttpOnly cookie.""",
                example = "a1b2c3d4e5f6g7h8i9j0"
        )
        String refreshToken
) {
}
