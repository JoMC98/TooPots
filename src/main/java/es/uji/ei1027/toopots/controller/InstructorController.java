package es.uji.ei1027.toopots.controller;

import es.uji.ei1027.toopots.daos.InstructorDao;
import es.uji.ei1027.toopots.daos.UsersDao;
import es.uji.ei1027.toopots.model.Instructor;
import es.uji.ei1027.toopots.model.Users;
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
@RequestMapping("/instructor")
public class InstructorController {
    private InstructorDao instructorDao;
    private UsersDao userDao;

    @Autowired
    public void setInstructorDao(InstructorDao instructorDao) {
        this.instructorDao=instructorDao;
    }

    @Autowired
    public void setUserDao(UsersDao userDao) {
        this.userDao = userDao;
    }

    //Llistar tots els instructors
    @RequestMapping("/list")
    public String listInstructors(Model model) {
        model.addAttribute("instructors", instructorDao.getInstructors());
        return "instructor/list";
    }

    //Afegir un nou instructor
    @RequestMapping("/add")
    public String addInstructor(Model model) {
        model.addAttribute("user", new Users());
        model.addAttribute("instructor", new Instructor());
        return "instructor/add";
    }

    //Processa la informació del add
    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(HttpSession session, @ModelAttribute("user") Users user,
                                   @ModelAttribute("instructor") Instructor instructor, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "instructor/add";

        user.setRol("Instructor");
        userDao.addUser(user);
        Users newUser = userDao.getUser(user.getUsername());

        session.setAttribute("user", newUser);

        instructorDao.addInstructor(instructor, newUser.getId());
        return "redirect:../";
    }

    //Actualitzar un instructor
    @RequestMapping(value="/update/{id}", method = RequestMethod.GET)
    public String editInstructor(Model model, @PathVariable int id) {
        model.addAttribute("instructor", instructorDao.getInstructor(id));
        return "instructor/update";
    }

    //Processa la informació del update
    @RequestMapping(value="/update/{id}", method = RequestMethod.POST)
    public String processUpdateSubmit(@PathVariable int id, @ModelAttribute("instructor") Instructor instructor, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "instructor/update";
        instructorDao.updateInstructor(instructor);
        return "redirect:../list";
    }

    //Esborra un instructor
    @RequestMapping(value="/delete/{id}")
    public String processDelete(@PathVariable int id) {
        instructorDao.deleteInstructor(id);
        return "redirect:../list";
    }
}
