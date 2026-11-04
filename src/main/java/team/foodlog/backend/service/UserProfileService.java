package team.foodlog.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.foodlog.backend.dto.userprofile.CreateUserProfileRequestDto;
import team.foodlog.backend.dto.userprofile.UpdateUserProfileRequestDto;
import team.foodlog.backend.dto.userprofile.UserProfileResponseDto;
import team.foodlog.backend.exception.EntityNotFoundException;
import team.foodlog.backend.exception.UserProfileAlreadyCreatedException;
import team.foodlog.backend.mapper.UserProfileMapper;
import team.foodlog.backend.model.UserProfile;
import team.foodlog.backend.repository.UserProfileRepository;
import team.foodlog.backend.security.AuthenticatedUserIdProvider;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    private final UserProfileMapper userProfileMapper;

    private final AuthenticatedUserIdProvider<Long> userIdProvider;

    @Transactional
    public UserProfileResponseDto create(CreateUserProfileRequestDto requestDto) {
        Long userId = userIdProvider.provide();

        if (userProfileRepository.existsById(userId)) {
            throw new UserProfileAlreadyCreatedException("Profile is already created");
        }

        UserProfile userProfile = userProfileMapper.toModel(
                requestDto,
                userId
        );

        return userProfileMapper.toDto(userProfileRepository.save(userProfile));
    }

    @Transactional
    public UserProfileResponseDto update(UpdateUserProfileRequestDto requestDto) {
        UserProfile userProfile = userProfileRepository
                .findById(userIdProvider.provide())
                .orElseThrow(
                        () -> new EntityNotFoundException(
                            "User profile was not found")
                );

        userProfileMapper.update(requestDto, userProfile);

        return userProfileMapper.toDto(userProfile);
    }

}
