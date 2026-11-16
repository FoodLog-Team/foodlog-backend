package team.foodlog.backend.service.meal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
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
@Slf4j
public class MealService {

    private static final int MAX_STORED_PHOTOS = 2;

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
        List<String> storedPhotos = new ArrayList<>(MAX_STORED_PHOTOS);

        registerRollbackCleanup(storedPhotos);

        MealEntry mealEntry = new MealEntry(userIdSupplier.get());

        Objects.requireNonNull(photo1, "At least one photo must be provided");

        mealEntryRepository.save(mealEntry);

        mealEntryPhotoRepository.save(
                createMealEntryPhoto(
                        photo1, mealEntry, MealEntryPhotoSlot.PRIMARY, storedPhotos)
        );

        if (photo2 != null) {
            mealEntryPhotoRepository.save(
                    createMealEntryPhoto(
                            photo2, mealEntry, MealEntryPhotoSlot.SECONDARY, storedPhotos)
            );
        }

        return new MealEntryCreatedResponseDto(
                mealEntry.getId(),
                MealAnalysisStatus.PENDING,
                mealEntry.getCreatedAt()
        );
    }

    private void registerRollbackCleanup(List<String> storedFiles) {
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        if (status != STATUS_ROLLED_BACK) {
                            return;
                        }

                        for (String storageKey : storedFiles) {
                            try {
                                photoStorage.delete(storageKey);
                            } catch (RuntimeException e) {
                                log.error(
                                        "Failed to cleanup photo after transaction rollback: {}",
                                        storageKey,
                                        e
                                );
                            }
                        }
                    }
                }
        );
    }

    private MealEntryPhoto createMealEntryPhoto(
            MultipartFile photo,
            MealEntry mealEntry,
            MealEntryPhotoSlot slot,
            List<String> storedPhotos
    ) {
        PhotoFormat format = photoFormatDetector.detect(photo);

        String storageKey = photoStorage.store(photo, mealEntry.getId(), format);

        storedPhotos.add(storageKey);

        return new MealEntryPhoto(
                mealEntry, slot, storageKey, format, photo.getSize()
        );
    }

}
