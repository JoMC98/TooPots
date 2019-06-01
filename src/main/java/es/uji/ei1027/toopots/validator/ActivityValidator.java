package es.uji.ei1027.toopots.validator;

import es.uji.ei1027.toopots.model.Activity;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ActivityValidator implements Validator {
    @Override
    public boolean supports(Class<?> cls) {
        return Activity.class.isAssignableFrom(cls);
    }
    @Override
    public void validate(Object obj, Errors errors) {
        Activity activity = (Activity) obj;

        if (activity.getName().trim().equals(""))
            errors.rejectValue("name", "obligatori",
                    "Cal introduir un valor");

        if (activity.getDates() == null)
            errors.rejectValue("dates", "obligatori",
                    "Cal introduir un valor");

        if (activity.getPlace().trim().equals(""))
            errors.rejectValue("place", "obligatori",
                    "Cal introduir un valor");

        if (activity.getMaxNumberPeople() == 0)
            errors.rejectValue("maxNumberPeople", "obligatori",
                    "Cal introduir un valor");

        if (activity.getPricePerPerson() == 0)
            errors.rejectValue("pricePerPerson", "obligatori",
                    "Cal introduir un valor");

        if (activity.getMeetingPoint().trim().equals(""))
            errors.rejectValue("meetingPoint", "obligatori",
                    "Cal introduir un valor");

        if (activity.getMeetingTime() == null)
            errors.rejectValue("meetingTime", "obligatori",
                    "Cal introduir un valor");

        if (activity.getDescription().trim().equals(""))
            errors.rejectValue("description", "obligatori",
                    "Cal introduir un valor");

        if (activity.getActivityType() == 0)
            errors.rejectValue("activityType", "obligatori",
                    "Cal introduir un valor");
    }
}
