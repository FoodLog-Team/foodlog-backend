package team.foodlog.backend.service.nutrition.target;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import team.foodlog.backend.dto.nutrition.profile.UserNutritionProfileDto;
import team.foodlog.backend.model.NutritionTarget;
import team.foodlog.backend.model.enums.TargetMode;
import team.foodlog.backend.repository.NutritionTargetRepository;
import team.foodlog.backend.service.nutrition.calculator.NutritionTargetCalculationRegistry;
import team.foodlog.backend.service.nutrition.calculator.NutritionTargetCalculationResult;

@Component
@RequiredArgsConstructor
public class NutritionTargetSynchronizator {

    private final NutritionTargetRepository nutritionTargetRepository;

    private final NutritionTargetCalculationRegistry calculationRegistry;

    @Transactional(propagation = Propagation.MANDATORY)
    public void synchronize(UserNutritionProfileDto profile, Long userId) {
        Optional<NutritionTarget> existing = nutritionTargetRepository.findById(userId);

        NutritionTarget nutritionTarget = existing
                    .orElseGet(() -> new NutritionTarget(userId));

        if (nutritionTarget.getTargetMode() == TargetMode.CUSTOM) {
            return;
        }

        NutritionTargetCalculationResult result = calculationRegistry
                .getRequired(nutritionTarget.getCalculationType())
                .calculate(profile, nutritionTarget.getFormulaType());

        nutritionTarget
                .setKcal(result.kcal())
                .setCarbs(result.carbs())
                .setProtein(result.protein())
                .setFats(result.fats());

        if (existing.isEmpty()) {
            nutritionTargetRepository.save(nutritionTarget);
        }
    }

}
