package team.foodlog.backend.service.photo;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import team.foodlog.backend.model.enums.PhotoFormat;

public interface PhotoStorage {

    String store(MultipartFile file, Long mealEntryId, PhotoFormat format);

    Resource get(String storageKey);

    void delete(String storageKey);

}
