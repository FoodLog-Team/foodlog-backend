package team.foodlog.backend.service.nutrition;

import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.foodlog.backend.dto.nutrition.target.CustomNutritionTargetRequestDto;
import team.foodlog.backend.dto.nutrition.target.NutritionTargetResponseDto;
import team.foodlog.backend.exception.CustomTargetNotConfiguredException;
import team.foodlog.backend.exception.EntityNotFoundException;
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
public class NutritionTargetService {

    private final NutritionTargetCalculationRegistry nutritionTargetCalculationRegistry;

    private final UserNutritionProfileService userNutritionProfileService;

    private final AuthenticatedUserIdSupplier<Long> userIdSupplier;

    private final NutritionTargetRepository nutritionTargetRepository;

    @Transactional
    public NutritionTargetResponseDto calculateRecommendations(
            NutritionTargetCalculationType calculationType, NutritionFormulaType formulaType
    ) {
        Long userId = userIdSupplier.get();

        NutritionTarget nutritionTarget = nutritionTargetRepository
                .findById(userId)
                .orElseGet(() -> new NutritionTarget(userId));

        NutritionTargetCalculationResult result = nutritionTargetCalculationRegistry
                .getRequired(calculationType)
                .calculate(userNutritionProfileService.getProfileDto(), formulaType);

        nutritionTarget
                .setRecommendedKcal(result.kcal())
                .setRecommendedCarbs(result.carbs())
                .setRecommendedProtein(result.protein())
                .setRecommendedFats(result.fats());

        nutritionTargetRepository.save(nutritionTarget);

        return toResponse(nutritionTarget, TargetMode.RECOMMENDED);
    }

    @Transactional(readOnly = true)
    public NutritionTargetResponseDto getNutritionTarget() {
        NutritionTarget nutritionTarget = findNutritionTarget();
        return toResponse(nutritionTarget, nutritionTarget.getMode());
    }

    @Transactional
    public NutritionTargetResponseDto saveCustom(CustomNutritionTargetRequestDto requestDto) {
        NutritionTarget nutritionTarget = findNutritionTarget();

        nutritionTarget
                .setMode(TargetMode.CUSTOM)
                .setCustomKcal(requestDto.kcal())
                .setCustomCarbs(requestDto.carbs())
                .setCustomProtein(requestDto.protein())
                .setCustomFats(requestDto.fats());

        return toResponse(nutritionTarget, TargetMode.CUSTOM);
    }

    @Transactional
    public void updateMode(TargetMode targetMode) {
        NutritionTarget nutritionTarget = findNutritionTarget();

        if (targetMode == TargetMode.CUSTOM && !hasCustomValues(nutritionTarget)) {
            throw new CustomTargetNotConfiguredException(
                    "Unable to set target mode 'custom' due to absence of custom target values");
        }
        nutritionTarget.setMode(targetMode);
    }

    private NutritionTargetResponseDto toResponse(
            NutritionTarget nutritionTarget, TargetMode targetMode) {
        if (targetMode == TargetMode.RECOMMENDED) {
            return new NutritionTargetResponseDto(
                nutritionTarget.getRecommendedKcal(),
                nutritionTarget.getRecommendedCarbs(),
                nutritionTarget.getRecommendedProtein(),
                nutritionTarget.getRecommendedFats()
            );
        }

        return new NutritionTargetResponseDto(
            nutritionTarget.getCustomKcal(),
            nutritionTarget.getCustomCarbs(),
            nutritionTarget.getCustomProtein(),
            nutritionTarget.getCustomFats()
        );
    }

    private boolean hasCustomValues(NutritionTarget nutritionTarget) {
        return Stream.of(
                nutritionTarget.getCustomKcal(),
                nutritionTarget.getCustomCarbs(),
                nutritionTarget.getCustomProtein(),
                nutritionTarget.getCustomFats()
        ).allMatch(Objects::nonNull);
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
