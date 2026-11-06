package team.foodlog.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import team.foodlog.backend.dto.nutrition.profile.CreateUserNutritionProfileRequestDto;
import team.foodlog.backend.dto.nutrition.profile.UpdateUserNutritionProfileRequestDto;
import team.foodlog.backend.dto.nutrition.profile.UserNutritionProfileResponseDto;
import team.foodlog.backend.service.nutrition.profile.UserNutritionProfileService;

@RestController
@RequestMapping("/nutrition-profile")
@RequiredArgsConstructor
public class UserNutritionProfileController {

    private final UserNutritionProfileService userNutritionProfileService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserNutritionProfileResponseDto create(
            @Valid @RequestBody CreateUserNutritionProfileRequestDto requestDto) {
        return userNutritionProfileService.create(requestDto);
    }

    @PatchMapping
    public UserNutritionProfileResponseDto update(
            @Valid @RequestBody UpdateUserNutritionProfileRequestDto requestDto
    ) {
        return userNutritionProfileService.update(requestDto);
    }

}
