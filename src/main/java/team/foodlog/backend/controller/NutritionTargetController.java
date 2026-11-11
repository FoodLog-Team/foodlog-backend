package team.foodlog.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import team.foodlog.backend.dto.nutrition.target.CustomNutritionTargetRequestDto;
import team.foodlog.backend.dto.nutrition.target.NutritionTargetModeRequestDto;
import team.foodlog.backend.dto.nutrition.target.NutritionTargetResponseDto;
import team.foodlog.backend.service.nutrition.NutritionTargetService;
import team.foodlog.backend.service.nutrition.enums.NutritionFormulaType;
import team.foodlog.backend.service.nutrition.enums.NutritionTargetCalculationType;

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
                    to estimate the Basal Metabolic Rate (BMR), then applies the activity level
                    and weight goal multipliers to derive the daily calorie target.
                    Macronutrient targets (carbs, protein, fats) are then distributed
                    proportionally.

                    This endpoint does NOT save the recommendations — it only returns the
                    calculated values. To apply them, ensure the target mode is set to
                    RECOMMENDED.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = """
                            Nutrition recommendations calculated successfully.
                            Returns the recommended target values.
                            """
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            Bad request. Invalid or missing query parameters.
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
            @Parameter(
                    description = """
                            The calculation type for nutrition targets.
                            Currently only BASIC is supported.
                            """,
                    example = "BASIC",
                    required = true
            )
            @RequestParam NutritionTargetCalculationType calculationType,
            @Parameter(
                    description = """
                            The formula used to calculate BMR (Basal Metabolic Rate).
                            Currently only MIFFLIN_ST_JEOR is supported.
                            """,
                    example = "MIFFLIN_ST_JEOR",
                    required = true
            )
            @RequestParam NutritionFormulaType formulaType
    ) {
        return nutritionTargetService.calculateRecommendations(calculationType, formulaType);
    }

    @Operation(
            summary = "Get current nutrition target",
            description = """
                    Returns the user's current active daily nutrition targets.

                    The returned values depend on the active target mode:
                    - RECOMMENDED: Returns auto-calculated targets based on the user's nutrition
                      profile.
                    - CUSTOM: Returns the manually set custom targets.

                    If no target mode is explicitly set, the default is RECOMMENDED.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = """
                            Current nutrition target retrieved successfully.
                            Returns either recommended or custom values depending on the
                            active target mode.
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
                            Not found. No nutrition target record exists for this user yet.
                            Calculate recommendations first via
                            POST /nutrition-target/recommendations.
                            """
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = """
                            Internal server error. An unexpected error occurred while
                            retrieving the target.
                            """
            )
    })
    @GetMapping
    public NutritionTargetResponseDto getNutritionTarget() {
        return nutritionTargetService.getNutritionTarget();
    }

    @Operation(
            summary = "Save custom nutrition target",
            description = """
                    Saves a custom daily nutrition target for the authenticated user.

                    The kcal value must be consistent with the macronutrient breakdown:
                    kcal ≈ (carbs × 4) + (protein × 4) + (fats × 9).

                    After saving custom targets, the user should switch the target mode to
                    CUSTOM via the PATCH /nutrition-target/mode endpoint to activate them.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = """
                            Custom nutrition target saved successfully.
                            Returns the saved target values.
                            Note: the target mode is automatically switched to CUSTOM after
                            saving.
                            """
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            Bad request. Invalid values or kcal is not consistent with macros.
                            All fields (kcal, carbs, protein, fats) are required and must be
                            within valid ranges.
                            The kcal value must approximately equal
                            (carbs × 4) + (protein × 4) + (fats × 9).
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
                            Not found. No nutrition target record exists for this user yet.
                            Calculate recommendations first via
                            POST /nutrition-target/recommendations.
                            """
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = """
                            Internal server error. An unexpected error occurred while saving
                            the custom target.
                            """
            )
    })
    @PutMapping("/custom")
    public NutritionTargetResponseDto saveCustomNutritionTarget(
            @Valid @RequestBody CustomNutritionTargetRequestDto requestDto
    ) {
        return nutritionTargetService.saveCustom(requestDto);
    }

    @Operation(
            summary = "Update nutrition target mode",
            description = """
                    Switches the active nutrition target mode between RECOMMENDED and CUSTOM.

                    - RECOMMENDED: Uses auto-calculated targets based on the user's nutrition
                      profile.
                    - CUSTOM: Uses the manually set custom targets (set via
                      PUT /nutrition-target/custom).

                    This endpoint does not change the target values themselves — it only
                    switches which set of values is considered active.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Target mode updated successfully. No content returned."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            Bad request. Invalid or missing mode value.
                            Mode must be one of: RECOMMENDED, CUSTOM.
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
                            Not found. No nutrition target record exists for this user yet.
                            Calculate recommendations first via
                            POST /nutrition-target/recommendations.
                            """
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = """
                            Conflict. Attempting to switch to CUSTOM mode, but no custom
                            target values have been saved yet. Save custom values first via
                            PUT /nutrition-target/custom.
                            """
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = """
                            Internal server error. An unexpected error occurred while updating
                            the target mode.
                            """
            )
    })
    @PatchMapping("/mode")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateNutritionTargetMode(
            @Valid @RequestBody NutritionTargetModeRequestDto requestDto
    ) {
        nutritionTargetService.updateMode(requestDto.mode());
    }

}
