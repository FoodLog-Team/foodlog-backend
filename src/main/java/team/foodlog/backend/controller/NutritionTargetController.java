package team.foodlog.backend.controller;

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

@RestController
@RequestMapping("/nutrition-target")
@RequiredArgsConstructor
public class NutritionTargetController {

    private final NutritionTargetService nutritionTargetService;

    @PostMapping("/recommendations")
    public NutritionTargetResponseDto calculateRecommendations(
            @RequestParam NutritionTargetCalculationType calculationType,
            @RequestParam NutritionFormulaType formulaType
    ) {
        return nutritionTargetService.calculateRecommendations(calculationType, formulaType);
    }

    @GetMapping
    public NutritionTargetResponseDto getNutritionTarget() {
        return nutritionTargetService.getNutritionTarget();
    }

    @PutMapping("/custom")
    public NutritionTargetResponseDto saveCustomNutritionTarget(
            @Valid @RequestBody CustomNutritionTargetRequestDto requestDto
    ) {
        return nutritionTargetService.saveCustom(requestDto);
    }

    @PatchMapping("/mode")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateNutritionTargetMode(
            @Valid @RequestBody NutritionTargetModeRequestDto requestDto
    ) {
        nutritionTargetService.updateMode(requestDto.mode());
    }

}
