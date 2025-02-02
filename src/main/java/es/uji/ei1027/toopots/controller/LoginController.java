package es.uji.ei1027.toopots.controller;

import es.uji.ei1027.toopots.daos.UsersDao;
import es.uji.ei1027.toopots.model.Users;
import es.uji.ei1027.toopots.validator.LoginValidator;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {
    @Autowired
    private UsersDao userDao;

    @RequestMapping("/login")
    public String login(Model model, HttpSession session) {
        session.setAttribute("userCanBeDeactivated", null);
        model.addAttribute("conexionRefused", false);
        model.addAttribute("user", new Users());
        return "login";
    }

    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String checkLogin(Model model, @ModelAttribute("user") Users user,
                             BindingResult bindingResult, HttpSession session) {
        session.setAttribute("userCanBeDeactivated", null);
        LoginValidator loginValidator = new LoginValidator();
        loginValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.toString());
            return "login";
        }

        // Comprova que el login siga correcte
        // intentant carregar les dades de l'usuari

        user = userDao.loadUserByUsername(user.getUsername(),user.getPasswd());
        if (user == null) {
            try {
                bindingResult.rejectValue("password", "badpw", "Contrasenya incorrecta");
            } catch (NotReadablePropertyException e) {
                model.addAttribute("user", new Users());
                model.addAttribute("conexionRefused", true);
                model.addAttribute("conexionRefusedReason", "BadPassword");
                return "login";
            }
        }

        // Autenticats correctament.
        if (user.getRol().equals("Request") || user.getRol().equals("Rejected") || user.getRol().equals("Fired")
                || user.getRol().equals("Deactivated")) {
            if (user.getRol().equals("Deactivated")) {
                session.setAttribute("userCanBeDeactivated", user.getId());
                model.addAttribute("userId", user.getId());
            }
            model.addAttribute("user", new Users());
            model.addAttribute("conexionRefused", true);
            model.addAttribute("conexionRefusedReason", user.getRol());
            return "login";
        } else {
            // Guardem les dades de l'usuari autenticat a la sessió
            session.setAttribute("user", user);
            String nextUrl = (String) session.getAttribute("nextUrl");
            if (nextUrl == null)
                return "redirect:/";
            else
                return "redirect:" + nextUrl;
        }
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
