package lux.fm.bookingservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = BookingDatesValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BookingDatesValid {
    String[] field();

    String message() default "Field checkIn must be earlier then checkOut, at least for one day";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
