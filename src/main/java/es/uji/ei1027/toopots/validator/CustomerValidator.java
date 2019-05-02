package es.uji.ei1027.toopots.validator;

import es.uji.ei1027.toopots.model.Customer;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.List;


public class CustomerValidator implements Validator {
    @Override
    public boolean supports(Class<?> cls) {
        return Customer.class.isAssignableFrom(cls);
    }

    @Override
    public void validate(Object obj, Errors errors) {

        Customer customer = (Customer) obj;

        if (customer.getBirthDate().equals(""))
            errors.rejectValue("birthDate", "obligatori",
                    "Cal introduir una data: DD/MM/AAAA");

        List<String> valors = Arrays.asList("Femeni", "Masculi");
        if (!valors.contains(customer.getSex()))
            errors.rejectValue("sex", "valor incorrecte",
                    "Deu ser: Femeni o Masculi");
    }
}
