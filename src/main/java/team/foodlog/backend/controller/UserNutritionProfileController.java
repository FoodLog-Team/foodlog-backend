package team.foodlog.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import team.foodlog.backend.dto.nutrition.profile.CreateUserNutritionProfileRequestDto;
import team.foodlog.backend.dto.nutrition.profile.UpdateUserNutritionProfileRequestDto;
import team.foodlog.backend.dto.nutrition.profile.UserNutritionProfileResponseDto;
import team.foodlog.backend.service.nutrition.profile.UserNutritionProfileService;

@Tag(
        name = "Nutrition Profile",
        description = """
                Endpoints for managing the user's nutrition profile.
                
                The nutrition profile stores the user's physical characteristics
                (date of birth, height, weight, sex) and fitness
                preferences (activity level, weight goal).
                This data is used to calculate personalized nutrition recommendations.
                
                A user must create a profile before they can receive nutrition recommendations.
                The profile can be partially updated at any time.
                
                Requires authentication via Bearer token in the Authorization header.
                """
)
@RestController
@RequestMapping("/nutrition-profile")
@RequiredArgsConstructor
public class UserNutritionProfileController {

    private final UserNutritionProfileService userNutritionProfileService;

    @Operation(
            summary = "Create nutrition profile",
            description = """
                    Creates a new nutrition profile for the authenticated user.
                    
                    All fields are required. The profile includes:
                    - Date of birth (user must be at least 18 years old)
                    - Height in centimeters (120–230 cm)
                    - Weight in kilograms (30–300 kg)
                    - Biological sex (MALE or FEMALE)
                    - Activity level (SEDENTARY to EXTRA_ACTIVE)
                    - Weight goal (LOSE_WEIGHT, MAINTAIN_WEIGHT, or GAIN_WEIGHT)
                    
                    Once created, the profile is used to calculate recommended nutrition targets.
                    If a profile already exists for the user, this request will fail.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Nutrition profile created successfully. "
                            + "Returns the created profile."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            Bad request. Invalid or missing fields.
                            All fields are required. User must be at least 18 years old.
                            Height must be 120–230 cm, weight must be 30–300 kg.
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
                    responseCode = "409",
                    description = """
                            Conflict. A nutrition profile already exists for this user.
                            Use PATCH /nutrition-profile to update the existing profile instead.
                            """
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = """
                            Internal server error.
                            An unexpected error occurred while creating the profile.
                            """
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserNutritionProfileResponseDto create(
            @Valid @RequestBody CreateUserNutritionProfileRequestDto requestDto) {
        return userNutritionProfileService.create(requestDto);
    }

    @Operation(
            summary = "Update nutrition profile",
            description = """
                    Partially updates the authenticated user's existing nutrition profile.
                    
                    Only the fields included in the request body will be updated. Fields that are
                    omitted will retain their current values. This allows updating individual
                    attributes (e.g., only the weight) without resubmitting the entire profile.
                    
                    If the user does not have an existing profile, this request will fail.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = """
                            Nutrition profile updated successfully. Returns the updated profile.
                            """
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            Bad request. Invalid field values.
                            For example: user under 18 years old, height outside 120–230 cm,
                            or weight outside 30–300 kg.
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
                    responseCode = "404",
                    description = """
                            Not found. No nutrition profile exists for this user.
                            Create a profile first via POST /nutrition-profile.
                            """
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = """
                            Internal server error.
                            An unexpected error occurred while updating the profile.
                            """
            )
    })
    @PatchMapping
    public UserNutritionProfileResponseDto update(
            @Valid @RequestBody UpdateUserNutritionProfileRequestDto requestDto) {
        return userNutritionProfileService.update(requestDto);
    }

}
