package team.foodlog.backend.service.meal;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team.foodlog.backend.dto.meal.MealEntryCreatedResponseDto;
import team.foodlog.backend.model.MealEntry;
import team.foodlog.backend.model.MealEntryPhoto;
import team.foodlog.backend.model.enums.MealAnalysisStatus;
import team.foodlog.backend.model.enums.MealEntryPhotoSlot;
import team.foodlog.backend.model.enums.PhotoFormat;
import team.foodlog.backend.model.enums.PlateSize;
import team.foodlog.backend.repository.MealEntryPhotoRepository;
import team.foodlog.backend.repository.MealEntryRepository;
import team.foodlog.backend.security.common.AuthenticatedUserIdSupplier;
import team.foodlog.backend.service.photo.PhotoFormatDetector;
import team.foodlog.backend.service.photo.PhotoStorage;

@Service
@RequiredArgsConstructor
public class MealService {

    private final MealEntryRepository mealEntryRepository;

    private final MealEntryPhotoRepository mealEntryPhotoRepository;

    private final PhotoStorage photoStorage;

    private final PhotoFormatDetector photoFormatDetector;

    private final AuthenticatedUserIdSupplier userIdSupplier;

    @Transactional
    public MealEntryCreatedResponseDto createEntry(
            MultipartFile photo1,
            MultipartFile photo2,
            PlateSize plateSize
    ) {
        MealEntry mealEntry = new MealEntry(userIdSupplier.get());

        Objects.requireNonNull(photo1, "At least one photo must be provided");

        mealEntryRepository.save(mealEntry);
        mealEntryPhotoRepository.save(
                createMealEntryPhoto(photo1, mealEntry, MealEntryPhotoSlot.PRIMARY)
        );

        if (photo2 != null) {
            mealEntryPhotoRepository.save(
                    createMealEntryPhoto(photo2, mealEntry, MealEntryPhotoSlot.SECONDARY)
            );
        }

        return new MealEntryCreatedResponseDto(
                mealEntry.getId(),
                MealAnalysisStatus.PENDING,
                mealEntry.getCreatedAt()
        );
    }

    private MealEntryPhoto createMealEntryPhoto(
            MultipartFile photo, MealEntry mealEntry, MealEntryPhotoSlot slot) {
        PhotoFormat format = photoFormatDetector.detect(photo);

        String storageKey = photoStorage.store(photo, mealEntry.getId(), format);
        return new MealEntryPhoto(
                mealEntry, slot, storageKey, format, photo.getSize()
        );
    }

}
