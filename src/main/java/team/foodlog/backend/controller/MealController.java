package team.foodlog.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import team.foodlog.backend.dto.meal.CreateMealEntryItemRequestDto;
import team.foodlog.backend.dto.meal.MealEntryCreatedResponseDto;
import team.foodlog.backend.dto.meal.MealEntryResponseDto;
import team.foodlog.backend.model.enums.PlateSize;
import team.foodlog.backend.service.meal.MealEntryItemService;
import team.foodlog.backend.service.meal.MealEntryService;

@Tag(
        name = "Meals",
        description = """
                Endpoints for creating meal entries and managing their food items.
                All endpoints require a valid Bearer access token.
                """
)
@RestController
@RequestMapping("/meals")
@RequiredArgsConstructor
public class MealController {

    private final MealEntryService mealEntryService;

    private final MealEntryItemService mealEntryItemService;

    @Operation(
            summary = "Create meal entry",
            description = """
                    Creates a meal entry from one or two photos. Food recognition and nutrition
                    lookup run asynchronously. Use the returned ID to poll
                    GET /meals/{mealEntryId} for the processing status and results.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "202",
                    description = "Meal entry created. Asynchronous processing has been queued.",
                    content = @Content(
                            schema = @Schema(implementation = MealEntryCreatedResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            Invalid photo, unsupported image format, or invalid plate size.
                            """
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. A valid Bearer access token is required."
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected error while creating the meal entry."
            )
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MealEntryCreatedResponseDto createEntry(
            @Parameter(
                    description = "Primary meal photo. JPEG, PNG, or WEBP; maximum size 10 MB.",
                    required = true
            )
            @RequestPart("photo1") MultipartFile photo1,
            @Parameter(
                    description = """
                            Optional second photo of the same meal from another angle.
                            JPEG, PNG, or WEBP; maximum size 10 MB.
                            """
            )
            @RequestPart(value = "photo2", required = false) MultipartFile photo2,
            @Parameter(
                    description = """
                            Approximate plate size used as a scale reference.
                            Defaults to MEDIUM.
                            """,
                    schema = @Schema(
                            defaultValue = "MEDIUM",
                            allowableValues = {"SMALL", "MEDIUM", "LARGE"}
                    )
            )
            @RequestParam(value = "plateSize", required = false, defaultValue = "MEDIUM")
            PlateSize plateSize
    ) {
        return mealEntryService.create(photo1, photo2, plateSize);
    }

    @Operation(
            summary = "Get meal entry",
            description = """
                    Returns the current state of a meal entry owned by the authenticated user,
                    including its processing status, food items, total weight, and nutrition
                    values. The result may be incomplete while processing is active.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Meal entry retrieved successfully.",
                    content = @Content(
                            schema = @Schema(implementation = MealEntryResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. A valid Bearer access token is required."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Meal entry was not found for the authenticated user."
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected error while retrieving the meal entry."
            )
    })
    @GetMapping("/{mealEntryId}")
    public MealEntryResponseDto getEntry(
            @Parameter(description = "ID of the meal entry.", example = "1", required = true)
            @PathVariable Long mealEntryId
    ) {
        return mealEntryService.getById(mealEntryId);
    }

    @Operation(
            summary = "Add food item manually",
            description = """
                    Adds a food item to an existing meal entry. Nutrition information is looked
                    up asynchronously using the provided food name. The endpoint returns
                    immediately; poll GET /meals/{mealEntryId} for the item status.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "202",
                    description = "Food item creation accepted. No response body is returned."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            Invalid request body. The name and positive weight are required.
                            """
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized. A valid Bearer access token is required."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Meal entry was not found for the authenticated user."
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Unexpected error while accepting the food item."
            )
    })
    @PostMapping("/products")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addItem(
            @Valid @RequestBody CreateMealEntryItemRequestDto requestDto
    ) {
        mealEntryItemService.create(requestDto);
    }

}
