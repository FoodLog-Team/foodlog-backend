package team.foodlog.backend.service.nutrition.provider;

public interface FoodValidator<T> {

    boolean isValid(T food);

}
