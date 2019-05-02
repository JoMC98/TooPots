package es.uji.ei1027.toopots.validator;

import es.uji.ei1027.toopots.model.Users;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class UserValidator implements Validator {
    @Override
    public boolean supports(Class<?> cls) {
        return Users.class.isAssignableFrom(cls);
    }
    @Override
    public void validate(Object obj, Errors errors) {
        Users user = (Users)obj;
        if (user.getUsername().trim().equals(""))
            errors.rejectValue("username", "obligatori",
                    "Cal introduir un valor");
        if (user.getPasswd().trim().equals(""))
            errors.rejectValue("password", "obligatori",
                    "Cal introduir un valor");

    }
}
