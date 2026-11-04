package team.foodlog.backend.exception;

public class UserProfileAlreadyCreatedException extends RuntimeException {

    public UserProfileAlreadyCreatedException(String message) {
        super(message);
    }
}
