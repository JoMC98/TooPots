package es.uji.ei1027.toopots.validator;

import es.uji.ei1027.toopots.model.Activity;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ActivityCancelationValidator implements Validator {
    @Override
    public boolean supports(Class<?> cls) {
        return Activity.class.isAssignableFrom(cls);
    }
    @Override
    public void validate(Object obj, Errors errors) {
        Activity activity = (Activity) obj;

        if (activity.getCancelationReason().trim().equals(""))
            errors.rejectValue("cancelationReason", "obligatori",
                    "Cal introduir un valor");

    }
}
