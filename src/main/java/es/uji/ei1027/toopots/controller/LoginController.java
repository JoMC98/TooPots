package es.uji.ei1027.toopots.controller;

import es.uji.ei1027.toopots.daos.UsersDao;
import es.uji.ei1027.toopots.model.Users;
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
    public String login(Model model) {
        model.addAttribute("user", new Users());
        return "login";
    }

    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String checkLogin(@ModelAttribute("user") Users user,
                             BindingResult bindingResult, HttpSession session) {
//        UserValidator userValidator = new UserValidator();
//        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "login";
        }

        // Comprova que el login siga correcte
        // intentant carregar les dades de l'usuari
        user = userDao.loadUserByUsername(user.getUsername(),user.getPasswd());
        if (user == null) {
            bindingResult.rejectValue("password", "badpw", "Contrasenya incorrecta");
            return "login";
        }
        // Autenticats correctament.
        // Guardem les dades de l'usuari autenticat a la sessió
        session.setAttribute("user", user);
        String nextUrl = (String) session.getAttribute("nextUrl");
        if (nextUrl == null)
            // Torna a la pàgina principal
            return "redirect:/";
        else
            return "redirect:" + nextUrl;
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
