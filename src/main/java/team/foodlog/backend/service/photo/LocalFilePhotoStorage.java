package team.foodlog.backend.service.photo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import team.foodlog.backend.exception.InvalidPhotoException;
import team.foodlog.backend.exception.PhotoNotFoundException;
import team.foodlog.backend.exception.PhotoStorageException;
import team.foodlog.backend.model.enums.PhotoFormat;

@Component
public class LocalFilePhotoStorage implements PhotoStorage {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    private final Path rootDirectory;

    public LocalFilePhotoStorage(@Value("${storage.meal-photos-directory}") String directory) {
        this.rootDirectory = Path.of(directory).toAbsolutePath().normalize();
    }

    @Override
    public String store(MultipartFile file, Long mealEntryId, PhotoFormat format) {
        if (file.isEmpty() || file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidPhotoException("Invalid photo size");
        }

        Path mealEntryDirectory = rootDirectory
                .resolve("meal-entries")
                .resolve(mealEntryId.toString())
                .normalize();

        if (!mealEntryDirectory.startsWith(rootDirectory)) {
            throw new IllegalArgumentException("Invalid storage path");
        }

        String filename = UUID.randomUUID() + "." + format.getExtension();

        String storageKey = "meal-entries/" + mealEntryId + "/" + filename;

        Path temp = null;

        try {
            Files.createDirectories(mealEntryDirectory);
            temp = Files.createTempFile(mealEntryDirectory, filename, ".part");

            try (
                    InputStream inputStream = file.getInputStream();
                    OutputStream outputStream = Files.newOutputStream(
                            temp,
                            StandardOpenOption.WRITE,
                            StandardOpenOption.TRUNCATE_EXISTING
                    )
            ) {
                inputStream.transferTo(outputStream);
            }

            Path target = mealEntryDirectory.resolve(filename).normalize();

            if (!target.startsWith(mealEntryDirectory)) {
                throw new IllegalArgumentException("Invalid target path");
            }

            try {
                Files.move(temp, target, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, target);
            }

            return storageKey;

        } catch (IOException e) {
            if (temp != null) {
                try {
                    Files.deleteIfExists(temp);
                } catch (IOException cleanupException) {
                    e.addSuppressed(cleanupException);
                }
            }

            throw new PhotoStorageException("Failed to store meal photo", e);
        }
    }

    @Override
    public Resource get(String storageKey) {
        Path path = resolve(storageKey);

        if (!Files.isRegularFile(path)) {
            throw new PhotoNotFoundException("Stored photo does no exist");
        }

        return new FileSystemResource(path);
    }

    @Override
    public void delete(String storageKey) {
        try {
            Files.deleteIfExists(resolve(storageKey));
        } catch (IOException e) {
            throw new PhotoStorageException("Failed to delete meal photo", e);
        }
    }

    private Path resolve(String storageKey) {
        Path path = rootDirectory.resolve(storageKey).normalize();

        if (!path.startsWith(rootDirectory)) {
            throw new IllegalArgumentException("Invalid storage key");
        }

        return path;
    }
}
