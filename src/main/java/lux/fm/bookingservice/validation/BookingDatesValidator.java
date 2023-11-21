package lux.fm.bookingservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.cglib.core.Local;

@RequiredArgsConstructor
public class BookingDatesValidator implements ConstraintValidator<BookingDatesValid, Object> {
    private String checkIn;
    private String checkOut;

    public void initialize(BookingDatesValid datesValid) {
        this.checkIn = datesValid.field()[0];
        this.checkOut = datesValid.field()[1];
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        Object checkInDate = new BeanWrapperImpl(object).getPropertyValue(checkIn);
        Object checkOutDate = new BeanWrapperImpl(object).getPropertyValue(checkOut);
        if(checkInDate == null) {
            return true;
        }

        if(checkOutDate == null) {
            return true;
        }

        return ((LocalDate) checkInDate).isBefore((LocalDate) checkOutDate);
    }
}
