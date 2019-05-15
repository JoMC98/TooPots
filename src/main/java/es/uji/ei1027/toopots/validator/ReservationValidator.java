package es.uji.ei1027.toopots.validator;

import es.uji.ei1027.toopots.model.Reservation;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ReservationValidator implements Validator {

    @Override
    public boolean supports(Class<?> cls) {
        return Reservation.class.isAssignableFrom(cls);
    }
    @Override
    public void validate(Object obj, Errors errors) {
        Reservation reservation = (Reservation) obj;

        if (reservation.getNumPeople() == 0) {
            errors.rejectValue("numUnder16", "obligatori",
                    "Cal introduir un valor");
            errors.rejectValue("numStudents", "obligatori",
                    "Cal introduir un valor");
            errors.rejectValue("numAdults", "obligatori",
                    "Cal introduir un valor");
            errors.rejectValue("numOver60", "obligatori",
                    "Cal introduir un valor");
        }

    }
}
