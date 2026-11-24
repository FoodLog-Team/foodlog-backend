package team.foodlog.backend.mapper;

import team.foodlog.backend.service.nutrition.calculator.NutritionInfo;

public interface ToNutritionInfoMapper<F> {

    NutritionInfo map(F from);

}
