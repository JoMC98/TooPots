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
    private final static int USER_AUTHORIZED = 2;
    private final static int USER_NOT_AUTHORIZED = 3;

    private UsersDao userDao;

    @Autowired
    public void setUserDao(UsersDao userDao) {
        this.userDao = userDao;
    }

    @RequestMapping("/")
    public String homepage(Model model) {
        return "index";
    }

    @RequestMapping("/admin/home")
    public String homepageAdmin(HttpSession session, Model model) {
        int acceso = controlarAcceso(session, "Admin");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/admin/home");
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            List<Users> users = userDao.getRequests();
            model.addAttribute("total", users.size());
            return "/admin/home";
        } else {
            return "redirect:/";
        }
    }

    private int controlarAcceso(HttpSession session, String rol) {
        if (session.getAttribute("user") == null) {
            return NOT_LOGGED;
        }
        Users user = (Users) session.getAttribute("user");
        if (user.getRol().equals(rol)) {
            return USER_AUTHORIZED;
        } else {
            return USER_NOT_AUTHORIZED;
        }
    }
}
