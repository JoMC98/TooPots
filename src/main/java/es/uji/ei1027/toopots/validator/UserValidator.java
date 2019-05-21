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
        Users user = (Users) obj;

        if (user.getName().trim().equals(""))
            errors.rejectValue("name", "obligatori",
                    "Cal introduir un valor");

        if (user.getMail().trim().equals(""))
            errors.rejectValue("mail", "obligatori",
                    "Cal introduir un valor");

        if (user.getNif().trim().equals(""))
            errors.rejectValue("nif", "obligatori",
                    "Cal introduir un valor");

        if (user.getUsername().trim().equals(""))
            errors.rejectValue("username", "obligatori",
                    "Cal introduir un valor");

        if (user.getPasswd() != null && user.getPasswd().trim().equals(""))
            errors.rejectValue("passwd", "obligatori",
                    "Cal introduir un valor");
    }
}

