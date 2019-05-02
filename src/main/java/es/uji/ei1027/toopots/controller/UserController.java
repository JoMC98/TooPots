package es.uji.ei1027.toopots.controller;

import es.uji.ei1027.toopots.daos.UsersDao;
import es.uji.ei1027.toopots.model.Users;
import org.jasypt.util.password.BasicPasswordEncryptor;
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
    public String processUpdatePasswdSubmit(@PathVariable int id, @ModelAttribute("newUser") Users newUser, BindingResult bindingResult) {

        Users user = userDao.getUser(id);

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
