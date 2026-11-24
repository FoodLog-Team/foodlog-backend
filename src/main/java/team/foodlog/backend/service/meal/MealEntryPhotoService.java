package team.foodlog.backend.service.meal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import team.foodlog.backend.model.MealEntry;
import team.foodlog.backend.model.MealEntryPhoto;
import team.foodlog.backend.model.enums.MealEntryPhotoSlot;
import team.foodlog.backend.model.enums.PhotoFormat;
import team.foodlog.backend.repository.MealEntryPhotoRepository;
import team.foodlog.backend.service.photo.PhotoFormatDetector;
import team.foodlog.backend.service.photo.PhotoStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class MealEntryPhotoService {

    private final MealEntryPhotoRepository mealEntryPhotoRepository;

    private final PhotoStorage photoStorage;

    private final PhotoFormatDetector photoFormatDetector;

    @Transactional
    public Long save(
            MealEntry mealEntry, MultipartFile photo, MealEntryPhotoSlot slot
    ) {
        PhotoFormat format = photoFormatDetector.detect(photo);
        String storageKey = photoStorage.store(photo, mealEntry.getId(), format);

        registerRollbackCleanup(storageKey);

        MealEntryPhoto mealEntryPhoto = new MealEntryPhoto(
                mealEntry, slot, storageKey, format, photo.getSize()
        );

        return mealEntryPhotoRepository.save(mealEntryPhoto).getId();

    }

    private void registerRollbackCleanup(String storageKey) {
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        if (status != STATUS_ROLLED_BACK) {
                            return;
                        }

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
        );
    }
}
