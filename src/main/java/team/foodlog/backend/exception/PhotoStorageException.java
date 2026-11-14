package team.foodlog.backend.exception;

public class PhotoStorageException extends RuntimeException {
    public PhotoStorageException(String message) {
        super(message);
    }

    public PhotoStorageException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
