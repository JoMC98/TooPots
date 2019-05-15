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

        if (activity.getDescription().trim().equals(""))
            errors.rejectValue("description", "obligatori",
                    "Cal introduir un valor");

    }
}
