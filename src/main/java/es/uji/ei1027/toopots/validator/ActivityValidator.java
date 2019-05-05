package es.uji.ei1027.toopots.validator;

import es.uji.ei1027.toopots.model.Activity;
import es.uji.ei1027.toopots.model.ActivityType;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.List;

public class ActivityValidator implements Validator {
    @Override
    public boolean supports(Class<?> cls) {
        return Activity.class.isAssignableFrom(cls);
    }
    @Override
    public void validate(Object obj, Errors errors) {
        Activity activity = (Activity) obj;

    }
}
