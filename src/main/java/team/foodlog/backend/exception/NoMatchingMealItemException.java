package team.foodlog.backend.exception;

public class NoMatchingMealItemException extends RuntimeException {
    public NoMatchingMealItemException(String message) {
        super(message);
    }
}
