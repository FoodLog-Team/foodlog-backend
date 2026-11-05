package team.foodlog.backend.dto.nutrition;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = CaloriesConsistentWithMacrosValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CaloriesConsistentWithMacros {

    String message() default "Calories amount is not consistent with macros";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
