package team.foodlog.backend.service.nutrition.target;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.foodlog.backend.dto.nutrition.target.CustomNutritionTargetRequestDto;
import team.foodlog.backend.dto.nutrition.target.NutritionTargetCalculationRequestDto;
import team.foodlog.backend.dto.nutrition.target.NutritionTargetResponseDto;
import team.foodlog.backend.exception.EntityNotFoundException;
import team.foodlog.backend.mapper.NutritionTargetMapper;
import team.foodlog.backend.model.NutritionTarget;
import team.foodlog.backend.model.enums.TargetMode;
import team.foodlog.backend.repository.NutritionTargetRepository;
import team.foodlog.backend.security.common.AuthenticatedUserIdSupplier;
import team.foodlog.backend.service.nutrition.calculator.NutritionTargetCalculationRegistry;
import team.foodlog.backend.service.nutrition.calculator.NutritionTargetCalculationResult;
import team.foodlog.backend.service.nutrition.enums.NutritionFormulaType;
import team.foodlog.backend.service.nutrition.enums.NutritionTargetCalculationType;
import team.foodlog.backend.service.nutrition.profile.UserNutritionProfileService;

@Service
@RequiredArgsConstructor
public class NutritionTargetService implements NutritionTargetProvider {

    private final NutritionTargetCalculationRegistry nutritionTargetCalculationRegistry;

    private final UserNutritionProfileService userNutritionProfileService;

    private final AuthenticatedUserIdSupplier userIdSupplier;

    private final NutritionTargetRepository nutritionTargetRepository;

    private final NutritionTargetMapper nutritionTargetMapper;

    @Transactional
    public NutritionTargetResponseDto calculateRecommendations(
            NutritionTargetCalculationRequestDto requestDto
    ) {
        Long userId = userIdSupplier.get();
        NutritionTargetCalculationType calculationType = requestDto.calculationType();
        NutritionFormulaType formulaType = requestDto.formulaType();

        NutritionTarget nutritionTarget = nutritionTargetRepository
                .findById(userId)
                .orElseGet(() -> new NutritionTarget(userId));

        NutritionTargetCalculationResult result = nutritionTargetCalculationRegistry
                .getRequired(calculationType)
                .calculate(userNutritionProfileService.getProfileDto(), formulaType);

        nutritionTarget
                .setKcal(result.kcal())
                .setCarbs(result.carbs())
                .setProtein(result.protein())
                .setFats(result.fats())
                .setTargetMode(TargetMode.RECOMMENDED)
                .setCalculationType(calculationType)
                .setFormulaType(formulaType);

        return nutritionTargetMapper.toDto(nutritionTargetRepository.save(nutritionTarget));
    }

    @Override
    @Transactional(readOnly = true)
    public NutritionTargetResponseDto getCurrentNutritionTarget() {
        return nutritionTargetMapper.toDto(findNutritionTarget());
    }

    @Transactional
    public NutritionTargetResponseDto saveCustom(CustomNutritionTargetRequestDto requestDto) {
        NutritionTarget nutritionTarget = findNutritionTarget();

        int kcal = requestDto.carbs() * 4
                + requestDto.protein() * 4
                + requestDto.fats() * 9;

        nutritionTarget
                .setKcal(kcal)
                .setCarbs(requestDto.carbs())
                .setProtein(requestDto.protein())
                .setFats(requestDto.fats())
                .setTargetMode(TargetMode.CUSTOM);

        return nutritionTargetMapper.toDto(nutritionTarget);
    }

    private NutritionTarget findNutritionTarget() {
        return nutritionTargetRepository
                .findById(userIdSupplier.get())
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Nutrition target was not calculated yet")
                );
    }

}
