package team.foodlog.backend.dto.auth;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Clock;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdultValidator implements ConstraintValidator<Adult, LocalDate> {

    private final Clock clock;

    @Override
    public boolean isValid(LocalDate dateOfBirth, ConstraintValidatorContext context) {
        if (dateOfBirth == null) {
            return true;
        }

        return !dateOfBirth.isAfter(LocalDate.now(clock).minusYears(18));
    }
}
