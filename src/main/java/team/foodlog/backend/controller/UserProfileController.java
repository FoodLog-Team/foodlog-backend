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
import team.foodlog.backend.dto.userprofile.CreateUserProfileRequestDto;
import team.foodlog.backend.dto.userprofile.UpdateUserProfileRequestDto;
import team.foodlog.backend.dto.userprofile.UserProfileResponseDto;
import team.foodlog.backend.service.UserProfileService;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserProfileResponseDto create(
            @Valid @RequestBody CreateUserProfileRequestDto requestDto) {
        return userProfileService.create(requestDto);
    }

    @PatchMapping
    public UserProfileResponseDto update(
            @Valid @RequestBody UpdateUserProfileRequestDto requestDto
    ) {
        return userProfileService.update(requestDto);
    }

}
