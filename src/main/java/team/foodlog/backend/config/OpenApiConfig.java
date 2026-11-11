package team.foodlog.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "FoodLog Backend API",
                description = """
                        RESTful API for the FoodLog nutrition tracking application.
                        
                        ## Authentication
                        
                        The API uses JWT (JSON Web Token) based authentication
                        with a dual-token approach:
                        
                        - **Access Token**: Short-lived JWT (15 minutes)
                            included in the `Authorization` header
                            as `Bearer <token>`.
                            Used to authenticate requests to protected endpoints.
                        
                        - **Refresh Token**: Long-lived opaque token (30 days)
                            stored in an HttpOnly cookie.
                            Used to obtain new access tokens without re-authentication.
                        
                        ### Workflow
                        
                        1. Register or login to receive an access token and a refresh token cookie.
                        2. Include the access token in the `Authorization` header
                           for protected endpoints.
                        3. When the access token expires, call `POST /auth/refresh`
                           to get a new one.
                        4. Call `POST /auth/logout` to end the current session.
                        5. Call `POST /auth/logout-all` to end all active sessions.
                        
                        ## Nutrition Profile
                        
                        Before using nutrition-related endpoints,
                        the user must create a nutrition profile
                        via `POST /nutrition-profile`. The profile stores physical
                        characteristics and fitness
                        goals used to calculate personalized nutrition recommendations.
                        
                        ## Nutrition Targets
                        
                        Users can receive auto-calculated nutrition recommendations
                        or set custom targets.
                        The active mode (RECOMMENDED or CUSTOM)
                        determines which values are returned by `GET /nutrition-target`.
                        """,
                version = "1.0.0",
                contact = @Contact(
                        name = "FoodLog Team"
                )
        )
)
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        in = SecuritySchemeIn.HEADER,
        description = "Enter your JWT access token obtained from /auth/register or /auth/login"
)
@Configuration
public class OpenApiConfig {
}
