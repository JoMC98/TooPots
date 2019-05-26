package es.uji.ei1027.toopots.exceptions;

import es.uji.ei1027.toopots.model.Users;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class TooPotsControllerAdvice {

    @ExceptionHandler(value = TooPotsException.class)
    public ModelAndView handleClubException(TooPotsException ex){

        ModelAndView mav = new ModelAndView("error/exceptionError");
        mav.addObject("message", ex.getMessage());
        mav.addObject("messageSecundari", ex.getMessageSecundari());
        mav.addObject("name", ex.getName());
        return mav;
    }
}
