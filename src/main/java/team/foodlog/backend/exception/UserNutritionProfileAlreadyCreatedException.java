package team.foodlog.backend.exception;

public class UserNutritionProfileAlreadyCreatedException extends RuntimeException {

    public UserNutritionProfileAlreadyCreatedException(String message) {
        super(message);
    }
}
