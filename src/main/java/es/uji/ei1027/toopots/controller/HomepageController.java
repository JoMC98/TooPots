package es.uji.ei1027.toopots.controller;

import es.uji.ei1027.toopots.daos.UsersDao;
import es.uji.ei1027.toopots.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/")
public class HomepageController {
    private final static int NOT_LOGGED = 1;
    private final static int ADMIN = 2;
    private final static int INSTRUCTOR = 3;
    private final static int CUSTOMER = 4;

    private UsersDao userDao;

    @Autowired
    public void setUserDao(UsersDao userDao) {
        this.userDao = userDao;
    }

    @RequestMapping("/")
    public String index(Model model) {
//        TODO ACTIVAR
//        int acceso = controlarAcceso(session);
//        if(acceso == NOT_LOGGED) {
//            return "redirect:activity/offer";
//        } else if (acceso == ADMIN) {
//            return "redirect:admin/home";
//        } else if (acceso == INSTRUCTOR){
//            return "redirect:instructor/listActivities";
//        } else {
//            return "redirect:activity/offer";
//        }
        return "index";
    }

    @RequestMapping("/home")
    public String homepage(HttpSession session, Model model) {
        int acceso = controlarAcceso(session);
        if(acceso == NOT_LOGGED) {
            return "redirect:activity/offer";
        } else if (acceso == ADMIN) {
            return "redirect:admin/home";
        } else if (acceso == INSTRUCTOR){
            return "redirect:instructor/listActivities/opened";
        } else {
            return "redirect:activity/offer";
        }
    }

    private int controlarAcceso(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return NOT_LOGGED;
        }
        Users user = (Users) session.getAttribute("user");
        if (user.getRol().equals("Admin")) {
            return ADMIN;
        }
        else if (user.getRol().equals("Instructor")) {
            return INSTRUCTOR;
        } else {
            return CUSTOMER;
        }
    }
}
