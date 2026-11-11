package team.foodlog.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import team.foodlog.backend.dto.auth.login.UserLoginRequestDto;
import team.foodlog.backend.dto.auth.register.UserRegistrationRequestDto;
import team.foodlog.backend.dto.auth.token.AccessTokenDto;
import team.foodlog.backend.dto.auth.token.TokenPairDto;
import team.foodlog.backend.security.common.AuthenticationService;
import team.foodlog.backend.security.refresh.RefreshTokenCookieFactory;

@Tag(
        name = "Authentication",
        description = """
                Authentication endpoints for user registration, login,
                access token refreshing and session management.
                
                Authentication uses a JWT access token and an opaque refresh token.
                The access token is returned in the response body.
                The refresh token is stored in an HttpOnly cookie.
                """
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class JwtAuthenticationController implements AuthenticationController<AccessTokenDto> {

    private final AuthenticationService<TokenPairDto> authenticationService;

    private final RefreshTokenCookieFactory cookieFactory;

    @Operation(
            summary = "Register a new user",
            description = """
                    Creates a new user account and automatically authenticates the user.
                    
                    Requirements:
                    - Email must be valid and unique.
                    - Password must contain at least 8 characters.
                    - Password must contain at least one uppercase letter.
                    - Password must contain at least one lowercase letter.
                    - Password must contain at least one special character.
                    - Password must contain at least one digit.
                    
                    Successful registration:
                    - Creates a new user account.
                    - Generates a JWT access token valid for 15 minutes.
                    - Generates an opaque refresh token valid for 30 days.
                    - Stores the refresh token in an HttpOnly cookie.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = """
                            User successfully registered.
                            Access token returned and refresh token
                            cookie set."""
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            Invalid registration data. Email or password does not satisfy
                            validation requirements. Check that email is a valid format and password
                            meets complexity rules (8-32 chars, uppercase, lowercase, digit, special
                            character)."""
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict. An account with this email already exists."
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = """
                            Internal server error. Mostly due to a race condition when two
                            identical registration requests arrive simultaneously
                            for the same email — behavior is identical to 409."""
            )
    })
    @Override
    @PostMapping("/register")
    public ResponseEntity<AccessTokenDto> register(
            @Valid @RequestBody UserRegistrationRequestDto requestDto,
            HttpServletResponse response) {
        TokenPairDto tokenPair = authenticationService.register(requestDto);

        this.addCookieHeader(response, tokenPair.refreshToken());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AccessTokenDto(tokenPair.accessToken()));
    }

    @Operation(
            summary = "Authenticate user",
            description = """
                    Authenticates an existing user using email and password.
                    
                    Successful authentication:
                    - Validates user credentials.
                    - Generates a JWT access token valid for 15 minutes.
                    - Generates an opaque refresh token valid for 30 days.
                    - Stores the refresh token in an HttpOnly cookie.
                    
                    The access token must be used in the Authorization header
                    when accessing protected endpoints.
                    
                    Authorization format:
                    Bearer <access_token>
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = """
                            Authentication successful. Access token returned and refresh token
                            cookie set.
                            """
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            Invalid request body.
                            Email or password fields are missing or malformed.
                            """
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. The provided email or password is incorrect. "
                            + "Returned when the user is not found or the password does not match."
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error. An unexpected error occurred during "
                            + "authentication."
            )
    })
    @Override
    @PostMapping("/login")
    public ResponseEntity<AccessTokenDto> login(
            @Valid @RequestBody UserLoginRequestDto requestDto,
            HttpServletResponse response) {
        TokenPairDto tokenPair = authenticationService.login(requestDto);

        this.addCookieHeader(response, tokenPair.refreshToken());

        return ResponseEntity.ok(
                new AccessTokenDto(tokenPair.accessToken())
        );
    }

    @Operation(
            summary = "Refresh access token",
            description = """
                    Generates a new access token and rotates the refresh token.
                    
                    Requires a valid refresh token stored in the HttpOnly cookie.
                    
                    Successful refresh:
                    - Validates the existing refresh token.
                    - Revokes the previous refresh token.
                    - Generates a new refresh token.
                    - Generates a new JWT access token.
                    - Updates the refresh token cookie.
                    
                    Refresh token lifetime: 30 days.
                    Access token lifetime: 15 minutes.
                    
                    Security:
                    - Refresh tokens cannot be reused after rotation.
                    - Expired or revoked refresh tokens are rejected.
                    - Refresh token reuse must not produce a valid token pair.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = """
                            Tokens successfully rotated. New access token returned and refresh
                            cookie updated.
                            """
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = """
                            Unauthorized. Possible reasons:
                            - Refresh token is missing from the request cookie.
                            - Refresh token has expired (valid for 30 days).
                            - Refresh token has been revoked (e.g. after logout or rotation).
                            - Refresh token reuse detected — all sessions for the user are revoked
                            as a security measure."""
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                            Not found. The refresh token does not exist in the database.
                            This may indicate the token was deleted, expired and cleaned up, or was
                            never created."""
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = """
                            Internal server error. An unexpected error occurred during token
                            rotation."""
            )
    })
    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenDto> refresh(
            @CookieValue(
                    name = RefreshTokenCookieFactory.COOKIE_NAME,
                    required = false
            ) String refreshToken,
            HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Refresh token is missing");
        }

        TokenPairDto tokenPair = authenticationService.refresh(refreshToken);

        this.addCookieHeader(response, tokenPair.refreshToken());

        return ResponseEntity.ok(
                new AccessTokenDto(tokenPair.accessToken())
        );
    }

    @Operation(
            summary = "Logout current session",
            description = """
                    Logs out the user's current session by revoking the refresh token
                    and clearing the refresh token cookie.
                    
                    After logout:
                    - The refresh token cookie is cleared (max-age=0).
                    - The refresh token is revoked server-side and can no longer be used.
                    - The access token remains valid until it expires (15 minutes).
                    
                    To immediately invalidate the access token as well, the client should
                    discard it after calling this endpoint.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = """
                            Logout successful. Refresh token revoked and cookie cleared.
                            No content returned.
                            """
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                            Not found. The refresh token provided in the cookie does not exist
                            in the database. The cookie will still be cleared on the client side."""
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = """
                            Internal server error.
                            An unexpected error occurred during logout.
                            """
            )
    })
    @Override
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(
                    name = RefreshTokenCookieFactory.COOKIE_NAME,
                    required = false
            ) String refreshToken,
            HttpServletResponse response) {
        this.clearCookieHeader(response);

        if (refreshToken != null && !refreshToken.isBlank()) {
            authenticationService.logout(refreshToken);
        }

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Logout all sessions",
            description = """
                    Logs out the user from all active sessions by revoking all refresh tokens
                    associated with the authenticated user and clearing the refresh token cookie.
                    
                    This is useful when the user wants to terminate all active sessions at once,
                    for example after changing their password or suspecting unauthorized access.
                    
                    After logout-all:
                    - All refresh tokens for the user are revoked server-side.
                    - The refresh token cookie is cleared (max-age=0).
                    - All existing access tokens remain valid until they expire (15 minutes).
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = """
                            All sessions logged out successfully.
                            All refresh tokens revoked and cookie cleared. No content returned.
                            """
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = """
                            Unauthorized. Authentication token is missing, expired, or invalid.
                            This endpoint requires a valid Bearer JWT access token.
                            """
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = """
                            Internal server error.
                            An unexpected error occurred during logout-all.
                            """
            )
    })
    @Override
    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(HttpServletResponse response) {
        this.clearCookieHeader(response);

        authenticationService.logoutAll();

        return ResponseEntity.noContent().build();
    }

    private void addCookieHeader(HttpServletResponse response, String token) {
        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookieFactory.create(token).toString()
        );
    }

    private void clearCookieHeader(HttpServletResponse response) {
        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookieFactory.clear().toString()
        );
    }

}
