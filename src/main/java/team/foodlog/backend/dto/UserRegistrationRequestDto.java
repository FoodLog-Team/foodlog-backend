package team.foodlog.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserRegistrationRequestDto(

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Length(min = 8, max = 32)
        String password
) {}
