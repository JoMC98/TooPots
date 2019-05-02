package es.uji.ei1027.toopots.validator;

import es.uji.ei1027.toopots.model.ActivityType;
import es.uji.ei1027.toopots.model.Users;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.List;

public class ActivityTypeValidator implements Validator {
    @Override
    public boolean supports(Class<?> cls) {
        return ActivityType.class.isAssignableFrom(cls);
    }
    @Override
    public void validate(Object obj, Errors errors) {
        ActivityType activityType = (ActivityType) obj;

        if (activityType.getName().trim().equals(""))
            errors.rejectValue("name", "obligatori",
                    "Cal introduir un valor");

        if (activityType.getLevel().trim().equals(""))
            errors.rejectValue("level", "obligatori",
                    "Cal introduir un nivell");

        List<String> valors = Arrays.asList("Baix", "Mitja", "Alt", "Extrem");
        if (!valors.contains(activityType.getLevel()))
            errors.rejectValue("level", "valor incorrecte",
                    "Has d'elegir un nivell");


    }
}
