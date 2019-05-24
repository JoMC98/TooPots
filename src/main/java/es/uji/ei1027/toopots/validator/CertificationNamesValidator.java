package es.uji.ei1027.toopots.validator;

import es.uji.ei1027.toopots.model.CertificationNames;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class CertificationNamesValidator implements Validator {
    private int numbFiles;


    public void setNumbFiles(int numbFiles) {
        this.numbFiles = numbFiles;
    }

    @Override
    public boolean supports(Class<?> cls) {
        return CertificationNames.class.isAssignableFrom(cls);
    }
    @Override
    public void validate(Object obj, Errors errors) {
        CertificationNames names = (CertificationNames) obj;

        if (names.getCertificate1().trim().equals(""))
            errors.rejectValue("certificate1", "obligatori",
                    "Cal introduir un valor");

        if (numbFiles>1 && names.getCertificate2().trim().equals(""))
            errors.rejectValue("certificate2", "obligatori",
                    "Cal introduir un valor");

        if (numbFiles>2 && names.getCertificate3().trim().equals(""))
            errors.rejectValue("certificate3", "obligatori",
                    "Cal introduir un valor");

        if (numbFiles>3 && names.getCertificate4().trim().equals(""))
            errors.rejectValue("certificate4", "obligatori",
                    "Cal introduir un valor");

    }
}
