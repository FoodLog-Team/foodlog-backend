package team.foodlog.backend.service.meal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team.foodlog.backend.dto.meal.MealEntryCreatedResponseDto;
import team.foodlog.backend.dto.meal.MealEntryItemResponseDto;
import team.foodlog.backend.dto.meal.MealEntryResponseDto;
import team.foodlog.backend.exception.EntityNotFoundException;
import team.foodlog.backend.mapper.MealEntryItemMapper;
import team.foodlog.backend.model.MealEntry;
import team.foodlog.backend.model.enums.AiConfidence;
import team.foodlog.backend.model.enums.MealAnalysisStatus;
import team.foodlog.backend.model.enums.MealEntryPhotoSlot;
import team.foodlog.backend.model.enums.MealEntryProcessingError;
import team.foodlog.backend.model.enums.MealItemFetchingStatus;
import team.foodlog.backend.model.enums.PlateSize;
import team.foodlog.backend.repository.MealEntryItemRepository;
import team.foodlog.backend.repository.MealEntryRepository;
import team.foodlog.backend.security.common.AuthenticatedUserIdSupplier;
import team.foodlog.backend.service.meal.recognition.FoodRecognitionRequestedEvent;
import team.foodlog.backend.service.meal.recognition.FoodRecognitionRequestedEventPublisher;
import team.foodlog.backend.service.nutrition.NutritionCalculator;
import team.foodlog.backend.service.nutrition.calculator.NutritionInfo;

@Service
@RequiredArgsConstructor
public class MealEntryService {

    private final MealEntryRepository mealEntryRepository;

    private final MealEntryPhotoService mealEntryPhotoService;

    private final AuthenticatedUserIdSupplier userIdSupplier;

    private final FoodRecognitionRequestedEventPublisher eventPublisher;

    private final MealEntryItemRepository mealEntryItemRepository;

    private final MealEntryItemMapper mealEntryItemMapper;

    @Transactional
    public MealEntryCreatedResponseDto create(
            MultipartFile photo1,
            MultipartFile photo2,
            PlateSize plateSize
    ) {
        Long userId = userIdSupplier.get();

        MealEntry mealEntry = new MealEntry(userId)
                    .setPlateSize(plateSize);

        Objects.requireNonNull(photo1, "At least one photo must be provided");

        mealEntryRepository.save(mealEntry);

        List<Long> photoIds = new ArrayList<>();

        photoIds.add(
                mealEntryPhotoService.save(mealEntry, photo1, MealEntryPhotoSlot.PRIMARY)
        );

        if (photo2 != null) {
            photoIds.add(
                    mealEntryPhotoService.save(mealEntry, photo2, MealEntryPhotoSlot.SECONDARY)
            );
        }

        eventPublisher.publish(
                new FoodRecognitionRequestedEvent(mealEntry.getId(), userId, photoIds, plateSize)
        );

        return new MealEntryCreatedResponseDto(
                mealEntry.getId(),
                MealAnalysisStatus.PENDING,
                mealEntry.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public MealEntryResponseDto getById(Long mealEntryId) {
        Long userId = userIdSupplier.get();

        MealEntry mealEntry = findMealEntry(mealEntryId, userId);
        List<MealEntryItemResponseDto> items = mealEntryItemRepository
                .findAllByMealEntryId(mealEntryId)
                .stream()
                .map(mealEntryItemMapper::toResponse)
                .toList();

        BigDecimal totalGrams = items.stream()
                .map(MealEntryItemResponseDto::grams)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<NutritionInfo> nutritions = items.stream()
                .filter(item -> item.status() == MealItemFetchingStatus.COMPLETED)
                .map(MealEntryItemResponseDto::total)
                .toList();

        return new MealEntryResponseDto(
                mealEntryId, mealEntry.getAnalysisStatus(),
                mealEntry.getProcessingError(),
                totalGrams,
                NutritionCalculator.calculateTotal(nutritions),
                items
        );
    }

    @Transactional
    public void updateAiConfidence(Long id, Long userId, AiConfidence aiConfidence) {
        findMealEntry(id, userId).setAiConfidence(aiConfidence);
    }

    @Transactional
    public void markProcessing(Long id, Long userId) {
        findMealEntry(id, userId)
                .setAnalysisStatus(MealAnalysisStatus.PROCESSING)
                .setProcessingError(null);
    }

    @Transactional
    public void markCompleted(Long id, Long userId) {
        findMealEntry(id, userId).setAnalysisStatus(MealAnalysisStatus.COMPLETED);
    }

    @Transactional
    public void markFailed(Long id, Long userId, MealEntryProcessingError error) {
        findMealEntry(id, userId)
                .setAnalysisStatus(MealAnalysisStatus.FAILED)
                .setProcessingError(error);
    }

    private MealEntry findMealEntry(Long id, Long userId) {
        return mealEntryRepository.findByIdAndUserId(id, userId).orElseThrow(
                () -> new EntityNotFoundException("Meal entry was not found")
        );
    }
}
