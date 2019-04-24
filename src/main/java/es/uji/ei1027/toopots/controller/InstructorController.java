package es.uji.ei1027.toopots.controller;

import es.uji.ei1027.toopots.daos.*;
import es.uji.ei1027.toopots.model.Activity;
import es.uji.ei1027.toopots.model.Certification;
import es.uji.ei1027.toopots.model.Instructor;
import es.uji.ei1027.toopots.model.Users;
import org.apache.commons.io.FilenameUtils;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/instructor")
public class InstructorController {
    private InstructorDao instructorDao;
    private UsersDao userDao;
    private ActivityDao activityDao;
    private CertificationDao certificationDao;
    private ActivityCertificationDao activityCertificationDao;
    private BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();

    @Value("${upload.file.directory}")
    private String uploadDirectory;

    @Autowired
    public void setInstructorDao(InstructorDao instructorDao) {
        this.instructorDao=instructorDao;
    }

    @Autowired
    public void setUserDao(UsersDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setActivityDao(ActivityDao activityDao){this.activityDao=activityDao;}

    @Autowired
    public void setCertificationDao(CertificationDao certificationDao){this.certificationDao=certificationDao;}

    @Autowired
    public void setActivityCertificationDao(ActivityCertificationDao activityCertificationDao){this.activityCertificationDao=activityCertificationDao;}


    //Llistar tots els instructors
    @RequestMapping("/list")
    public String listInstructors(Model model) {
        List<Users> users = userDao.getInstructors();
        List<Instructor> instructors = new ArrayList<Instructor>();
        for (Users us: users) {
            Instructor ins = instructorDao.getInstructor(us.getId());
            ins.setName(us.getName());
            ins.setActivities(activityCertificationDao.getAuthorizations(us.getId()));
            instructors.add(ins);
        }
        model.addAttribute("instructors", instructors);
        return "instructor/list";
    }

    //Llistar totes les sol·licituds

    @RequestMapping("/listRequests")
    public String listRequests(Model model) {
        List<Users> users = userDao.getRequests();
        List<Instructor> instructors = new ArrayList<Instructor>();
        for (Users us: users) {
            Instructor ins = instructorDao.getInstructor(us.getId());
            ins.setName(us.getName());
            ins.setCertifications(certificationDao.getCertifications(us.getId()));
            instructors.add(ins);
        }
        model.addAttribute("instructors", instructors);
        return "instructor/listRequests";
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
    public String processAddSubmit(HttpSession session, @RequestParam("foto") MultipartFile foto,
                                   @ModelAttribute("user") Users user, @ModelAttribute("instructor") Instructor instructor,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "instructor/add";

        if (foto.isEmpty()) {
            // Enviar mensaje de error porque no hay fichero seleccionado
            return "instructor/add";
        }


        user.setRol("Instructor");
        user.setPasswd(passwordEncryptor.encryptPassword(user.getPasswd()));

        userDao.addUser(user);
        Users newUser = userDao.getUser(user.getUsername());

        session.setAttribute("user", newUser);

        //TODO FICAR FOTO BE


        try {
            // Obtener el fichero y guardarlo
            byte[] bytes = foto.getBytes();

            String extension = FilenameUtils.getExtension(foto.getOriginalFilename());
            String direccion = "images/instructorProfiles/" + newUser.getId() + "." + extension;

            Path path = Paths.get(uploadDirectory + direccion);
            instructor.setPhoto("/" + direccion);

            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }


        instructorDao.addInstructor(instructor, newUser.getId());

        return "redirect:../";
    }

    //Actualitzar un instructor
    @RequestMapping(value="/update/{id}", method = RequestMethod.GET)
    public String editInstructor(Model model, @PathVariable int id) {
        model.addAttribute("user", userDao.getUser(id));
        model.addAttribute("instructor", instructorDao.getInstructor(id));
        return "instructor/update";
    }

    //Processa la informació del update
    @RequestMapping(value="/update/{id}", method = RequestMethod.POST)
    public String processUpdateSubmit(@PathVariable int id, @ModelAttribute("user") Users user,
                                      @ModelAttribute("instructor") Instructor instructor, BindingResult bindingResult) {

        if (bindingResult.hasErrors())
            return "instructor/update";

        userDao.updateUser(user);
        instructorDao.updateInstructor(instructor);
        return "redirect:../list";
    }

    //Esborra un instructor
    @RequestMapping(value="/delete/{id}")
    public String processDelete(@PathVariable int id) {
        userDao.deleteUser(id);
        instructorDao.deleteInstructor(id);
        return "redirect:../list";
    }

    //Veure perfil monitor
    @RequestMapping("/profile/{id}")
    public String seeInstructor(Model model, @PathVariable int id) {
        Instructor ins = instructorDao.getInstructor(id);
        ins.setCertifications(certificationDao.getCertifications(id));
        ins.setActivities(activityCertificationDao.getAuthorizations(id));
        model.addAttribute("user", userDao.getUser(id));
        model.addAttribute("instructor", ins);
        return "instructor/profile";
    }

    //Veure solicitud monitor
    @RequestMapping("/request/{id}")
    public String seeInstructorRequest(Model model, @PathVariable int id) {
        Instructor ins = instructorDao.getInstructor(id);
        ins.setCertifications(certificationDao.getCertifications(id));
        model.addAttribute("user", userDao.getUser(id));
        model.addAttribute("instructor", ins);
        return "instructor/request";
    }

    //Acceptar solicitud monitor
    @RequestMapping("/accept/{id}")
    public String acceptInstructorRequest(Model model, @PathVariable int id) {
        Users user = userDao.getUser(id);
        userDao.updateRole(id, "Instructor");
        return "instructor/listRequests";
    }

    //Rebutjar solicitud monitor
    @RequestMapping("/reject/{id}")
    public String rejectInstructorRequest(Model model, @PathVariable int id) {
        Users user = userDao.getUser(id);
//       TODO ROL REJECTED
//        userDao.updateRole(id, "Rejected");
        userDao.updateRole(id, "Instructor");
        return "instructor/ListRequests";
    }


    //Processa la informació del profile
    @RequestMapping(value="/profile/{id}", method = RequestMethod.POST)
    public String processProfileSubmit(@PathVariable int id, @ModelAttribute("user") Users user,
                                      @ModelAttribute("instructor") Instructor instructor, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "instructor/profile";

        return "redirect:../asignarActivitat/{id}";
    }

    //Veure activitats disponibles en el desplegable

    @RequestMapping(value="/asignarActivitat/{id}", method= RequestMethod.GET)
    public String asignarActivitats(Model model, @PathVariable int id){
        model.addAttribute("users", userDao.getInstructors());
        model.addAttribute("instructors", instructorDao.getInstructors());
        model.addAttribute("user", userDao.getUser(id));
        model.addAttribute("instructor", instructorDao.getInstructor(id));
        model.addAttribute("activitat", new Activity());
        model.addAttribute("noms", activityDao.getActivities());
        return "instructor/asignarActivitat";
    }

    //Processa la informació del assignar activitat
    @RequestMapping(value="/asignarActivitat/{id}", method = RequestMethod.POST)
    public String processAsignarSubmit(@PathVariable int id, @ModelAttribute("user") Users user,
                                      @ModelAttribute("instructor") Instructor instructor,
                                       @ModelAttribute("activity") Activity activity,
                                       BindingResult bindingResult) {

        if (bindingResult.hasErrors())
            return "instructor/asignarActivitat";

        userDao.updateUser(user);
        instructorDao.updateInstructor(instructor);
        activityDao.updateActivity(activity);
        return "redirect:instructor/profile";
    }
}
