package team.foodlog.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import team.foodlog.backend.dto.meal.MealEntryCreatedResponseDto;
import team.foodlog.backend.model.enums.PlateSize;
import team.foodlog.backend.service.meal.MealService;

@RestController
@RequestMapping("/meals")
@RequiredArgsConstructor
public class MealController {

    private final MealService mealService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public MealEntryCreatedResponseDto createEntry(
            @RequestPart("photo1") MultipartFile photo1,
            @RequestPart(value = "photo2", required = false) MultipartFile photo2,
            @RequestParam(value = "plateSize", required = false, defaultValue = "MEDIUM")
            PlateSize plateSize
    ) {
        return mealService.createEntry(photo1, photo2, plateSize);
    }

}
