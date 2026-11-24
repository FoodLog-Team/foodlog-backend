package team.foodlog.backend.exception;

public class InvalidMealItemNameException extends RuntimeException {
    public InvalidMealItemNameException(String message) {
        super(message);
    }
}
