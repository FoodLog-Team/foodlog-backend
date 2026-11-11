package team.foodlog.backend.dto.auth.token;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = """
                Response containing a JWT access token.
                The access token is short-lived (15 minutes) and must be included in the
                Authorization header as 'Bearer <token>' for protected endpoints."""
)
public record AccessTokenDto(

        @Schema(
                description = """
                        JWT access token string. Valid for 15 minutes.
                        Use it in the Authorization header: Bearer <accessToken>""",
                example = """
                        eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.doz
                        jgNryP4J3jVmNHl0w5N_XgL0n3I9PlFUP0THsR8U"""
        )
        String accessToken
) {}
