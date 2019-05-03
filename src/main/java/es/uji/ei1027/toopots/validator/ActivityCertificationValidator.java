package es.uji.ei1027.toopots.validator;

import es.uji.ei1027.toopots.model.ActivityCertification;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.List;

public class ActivityCertificationValidator implements Validator {
    @Override
    public boolean supports(Class<?> cls) {
        return ActivityCertificationValidator.class.isAssignableFrom(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {

        ActivityCertification activityCertification = (ActivityCertification) obj;

        if (activityCertification.getActivityType() == 0)
            errors.rejectValue("activityType", "obligatori",
                    "Cal introduir un valor");

        if (activityCertification.getIdCertification() == 0)
            errors.rejectValue("idCertification", "obligatori",
                    "Cal introduir un valor");
    }
}
