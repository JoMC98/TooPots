package es.uji.ei1027.toopots.controller;

import es.uji.ei1027.toopots.daos.UsersDao;
import es.uji.ei1027.toopots.exceptions.LoginException;
import es.uji.ei1027.toopots.model.Users;
import es.uji.ei1027.toopots.validator.LoginValidator;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;


@Controller
@RequestMapping("/user")
public class UserController {
    private UsersDao userDao;
    private BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();

    @Autowired
    public void setUserDao(UsersDao userDao) {
        this.userDao = userDao;
    }

    //Actualitzar contrasenya
    @RequestMapping(value="/updatePasswd", method = RequestMethod.GET)
    public String editPasswd(Model model, HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        model.addAttribute("newUser", userDao.getUser(user.getId()));
        return "user/updatePasswd";
    }

    //Processa la informació de la actualització de contrasenya
    @RequestMapping(value="/updatePasswd", method = RequestMethod.POST)
    public String processUpdatePasswdSubmit(HttpSession session, @ModelAttribute("newUser") Users newUser, BindingResult bindingResult) {
        Users user = (Users) session.getAttribute("user");

        LoginValidator loginValidator = new LoginValidator();
        loginValidator.validate(newUser, bindingResult);
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.toString());
            return "user/updatePasswd";
        }

        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();

        boolean ret = passwordEncryptor.checkPassword(newUser.getUsername(), user.getPasswd());

        if (ret) {
            user.setPasswd(passwordEncryptor.encryptPassword(newUser.getPasswd()));
            userDao.updatePassword(user);
            return "redirect:/";
        }
        else {
            return "user/updatePasswd";
        }
    }

}
