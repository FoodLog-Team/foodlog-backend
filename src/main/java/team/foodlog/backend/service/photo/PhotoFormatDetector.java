package team.foodlog.backend.service.photo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import team.foodlog.backend.exception.InvalidPhotoException;
import team.foodlog.backend.model.enums.PhotoFormat;

@Component
public class PhotoFormatDetector {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    private final Tika tika = new Tika();

    public PhotoFormat detect(MultipartFile photo) {
        if (photo == null || photo.isEmpty()) {
            throw new InvalidPhotoException("Photo is missing");
        }

        if (photo.getSize() > MAX_FILE_SIZE) {
            throw new InvalidPhotoException("Photo exceeds 10MB");
        }

        try (InputStream inputStream = photo.getInputStream()) {
            String contentType = tika.detect(inputStream);

            return Arrays.stream(PhotoFormat.values())
                    .filter(format -> format.getContentType().equals(contentType))
                    .findFirst()
                    .orElseThrow(() -> new InvalidPhotoException(
                            "Unsupported photo format: " + contentType
                    ));

        } catch (IOException e) {
            throw new InvalidPhotoException("Unable to detect photo format");
        }
    }

}
