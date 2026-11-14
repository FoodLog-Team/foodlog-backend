package team.foodlog.backend.service.nutrition.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.foodlog.backend.dto.nutrition.profile.CreateUserNutritionProfileRequestDto;
import team.foodlog.backend.dto.nutrition.profile.UpdateUserNutritionProfileRequestDto;
import team.foodlog.backend.dto.nutrition.profile.UserNutritionProfileDto;
import team.foodlog.backend.dto.nutrition.profile.UserNutritionProfileResponseDto;
import team.foodlog.backend.exception.EntityNotFoundException;
import team.foodlog.backend.exception.UserNutritionProfileAlreadyCreatedException;
import team.foodlog.backend.mapper.UserNutritionProfileMapper;
import team.foodlog.backend.model.UserNutritionProfile;
import team.foodlog.backend.repository.UserNutritionProfileRepository;
import team.foodlog.backend.security.common.AuthenticatedUserIdSupplier;

@Service
@RequiredArgsConstructor
public class UserNutritionProfileService {

    private final UserNutritionProfileRepository userNutritionProfileRepository;

    private final UserNutritionProfileMapper userNutritionProfileMapper;

    private final AuthenticatedUserIdSupplier userIdSupplier;

    @Transactional
    public UserNutritionProfileResponseDto create(CreateUserNutritionProfileRequestDto requestDto) {
        Long userId = userIdSupplier.get();

        if (userNutritionProfileRepository.existsById(userId)) {
            throw new UserNutritionProfileAlreadyCreatedException("Profile is already created");
        }

        UserNutritionProfile userNutritionProfile = userNutritionProfileMapper.toModel(
                requestDto,
                userId
        );

        return userNutritionProfileMapper
                .toResponseDto(userNutritionProfileRepository.save(userNutritionProfile));
    }

    @Transactional
    public UserNutritionProfileResponseDto update(UpdateUserNutritionProfileRequestDto requestDto) {
        UserNutritionProfile userNutritionProfile = userNutritionProfileRepository
                .findById(userIdSupplier.get())
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Profile was not found")
                );

        userNutritionProfileMapper.update(requestDto, userNutritionProfile);

        return userNutritionProfileMapper.toResponseDto(userNutritionProfile);
    }

    @Transactional(readOnly = true)
    public UserNutritionProfileDto getProfileDto() {
        return userNutritionProfileMapper.toDto(
                userNutritionProfileRepository.findById(userIdSupplier.get())
                        .orElseThrow(
                                () -> new IllegalStateException(
                                        "User nutrition profile was not created yet")
                        )
        );
    }

}
