package es.uji.ei1027.toopots.validator;

import es.uji.ei1027.toopots.model.Instructor;
import es.uji.ei1027.toopots.model.Users;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class InstructorValidator  implements Validator {

    @Override
    public boolean supports(Class<?> cls) {
        return Instructor.class.isAssignableFrom(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {

        System.out.println(obj);
        Instructor instructor = (Instructor) obj;

        if (instructor.getName().trim().equals(""))
            errors.rejectValue("name", "obligatori",
                    "Cal introduir un valor");

        if (instructor.getResidence().trim().equals(""))
            errors.rejectValue("residence", "obligatori",
                    "Cal introduir un valor");


        if (instructor.getAccountNumber() == 0)
            errors.rejectValue("accountNumber", "obligatori",
                    "Cal introduir un valor");

    }
}
