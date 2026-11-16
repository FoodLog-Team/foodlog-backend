package team.foodlog.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.foodlog.backend.dto.nutrition.target.CustomNutritionTargetRequestDto;
import team.foodlog.backend.dto.nutrition.target.NutritionTargetCalculationRequestDto;
import team.foodlog.backend.dto.nutrition.target.NutritionTargetResponseDto;
import team.foodlog.backend.service.nutrition.target.NutritionTargetService;

@Tag(
        name = "Nutrition Target",
        description = """
                Endpoints for managing daily nutrition targets
                (calories, carbs, protein, fats).

                Users can either use recommended targets (auto-calculated from their nutrition
                profile) or set custom targets manually. The active mode determines which values
                are returned by the GET endpoint.

                Requires authentication via Bearer token in the Authorization header.
                """
)
@RestController
@RequestMapping("/nutrition-target")
@RequiredArgsConstructor
public class NutritionTargetController {

    private final NutritionTargetService nutritionTargetService;

    @Operation(
            summary = "Calculate nutrition recommendations",
            description = """
                    Calculates recommended daily nutrition targets based on the authenticated
                    user's nutrition profile (age, height, weight, sex, activity level, and
                    weight goal).

                    The calculation uses the specified formula type (e.g., Mifflin-St Jeor)
                    to estimate the Basal Metabolic Rate (BMR), and nutrition
                    target calculation strategy, then applies the activity level
                    and weight goal multipliers to derive the daily calorie target.
                    Macronutrient targets (carbs, protein, fats) are then distributed
                    proportionally.

                    The selected formula type and calculation type are saved
                    as the user's calculation preferences.
                    
                    After successful calculation, the target mode is automatically
                    switched to RECOMMENDED.
                    
                    Subsequent GET requests will recalculate recommendations
                    using the latest nutrition profile.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = """
                            Nutrition recommendations calculated successfully
                            and saved. The target mode is now RECOMMENDED.
                            """,
                    content = @Content(
                            schema = @Schema(
                                    implementation = NutritionTargetResponseDto.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            Bad request. Missing or invalid calculation parameters.
                            Both calculationType and formulaType are required and must be
                            valid enum values.
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
                            Not found. The user's nutrition profile does not exist.
                            Create a nutrition profile first via POST /nutrition-profile
                            before calculating recommendations.
                            """
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = """
                            Internal server error. An unexpected error occurred during
                            calculation.
                            """
            )
    })
    @PostMapping("/recommendations")
    public NutritionTargetResponseDto calculateRecommendations(
            @Valid @RequestBody NutritionTargetCalculationRequestDto requestDto
    ) {
        return nutritionTargetService.calculateRecommendations(requestDto);
    }

    @Operation(
            summary = "Get current nutrition target",
            description = """
                    Returns the user's currently active daily nutrition targets.

                    The returned values depend on the active target mode:

                    - RECOMMENDED: Nutrition targets are recalculated on every request
                      using the latest nutrition profile and previously saved
                      calculation preferences.

                    - CUSTOM: Returns the manually configured nutrition targets
                      saved by the user.

                    The response always includes the active target mode,
                    formula type and calculation type.

                    Recalculated recommended values are returned but not
                    updating the persisted target values.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Current nutrition target retrieved successfully.",
                    content = @Content(
                            schema = @Schema(
                                    implementation = NutritionTargetResponseDto.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. Missing, expired or invalid JWT token."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                            Not found. No nutrition target configuration exists
                            for the authenticated user, or the nutrition profile
                            required for recommendation calculation does not exist.
                            """
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = """
                            Internal server error while retrieving nutrition targets.
                            """
            )
    })
    @GetMapping
    public NutritionTargetResponseDto getCurrentNutritionTarget() {
        return nutritionTargetService.getCurrentNutritionTarget();
    }

    @Operation(
            summary = "Save custom nutrition target",
            description = """
                    Saves manually configured daily nutrition targets.

                    After successful saving, the target mode is automatically
                    switched to CUSTOM.

                    Previously saved formula type and calculation type are preserved.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = """
                            Custom nutrition target saved successfully.
                            The target mode is now CUSTOM.
                            """,
                    content = @Content(
                            schema = @Schema(
                                    implementation = NutritionTargetResponseDto.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            Bad request. Invalid target values.

                            All fields are required and must be within their
                            allowed ranges.
                            """
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. Missing, expired or invalid JWT token."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                            No nutrition target configuration exists for this user yet.
                            Calculate recommendations first.
                            """
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = """
                            Internal server error while saving custom nutrition targets.
                            """
            )
    })
    @PutMapping("/custom")
    public NutritionTargetResponseDto saveCustomNutritionTarget(
            @Valid @RequestBody CustomNutritionTargetRequestDto requestDto
    ) {
        return nutritionTargetService.saveCustom(requestDto);
    }

}
