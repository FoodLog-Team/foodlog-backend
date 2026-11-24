package team.foodlog.backend.exception;

public class NutritionInfoNotFoundException extends RuntimeException {
    public NutritionInfoNotFoundException(String message) {
        super(message);
    }
}
