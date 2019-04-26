package es.uji.ei1027.toopots.controller;

import es.uji.ei1027.toopots.daos.*;
import es.uji.ei1027.toopots.model.*;
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
    private final static int NOT_LOGGED = 1;
    private final static int USER_AUTHORIZED = 2;
    private final static int USER_NOT_AUTHORIZED = 3;

    private InstructorDao instructorDao;
    private UsersDao userDao;
    private ActivityDao activityDao;
    private ActivityTypeDao activityTypeDao;
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
    public void setActivityTypeDao(ActivityTypeDao activityTypeDao){this.activityTypeDao=activityTypeDao;}

    @Autowired
    public void setCertificationDao(CertificationDao certificationDao){this.certificationDao=certificationDao;}

    @Autowired
    public void setActivityCertificationDao(ActivityCertificationDao activityCertificationDao){this.activityCertificationDao=activityCertificationDao;}


    /*PART QUE UTILITZA EL ADMINISTRADOR*/

    //Llistar tots els instructors
    @RequestMapping("/list")
    public String listInstructors(HttpSession session, Model model) {
        int acceso = controlarAcceso(session, "Admin");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/instructor/list");
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
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
        } else {
            return "redirect:/";
        }
    }

    //Llistar totes les sol·licituds

    @RequestMapping("/listRequests")
    public String listRequests(HttpSession session, Model model) {
        int acceso = controlarAcceso(session, "Admin");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/instructor/listRequests");
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
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
        } else {
            return "redirect:/";
        }
    }

    //Esborra un instructor
    @RequestMapping(value="/delete/{id}")
    public String processDelete(HttpSession session, Model model, @PathVariable int id) {
        int acceso = controlarAcceso(session, "Admin");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/instructor/delete/" + id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            userDao.deleteUser(id);
            instructorDao.deleteInstructor(id);
            return "redirect:../list";
        } else {
            return "redirect:/";
        }
    }

    //Veure perfil monitor
    @RequestMapping("/profile/{id}")
    public String seeInstructor(HttpSession session, Model model, @PathVariable int id) {
        int acceso = controlarAcceso(session, "Admin");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/instructor/profile/" + id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            Users user = userDao.getUser(id);
            if (user.getRol().equals("Instructor")) {
                Instructor ins = instructorDao.getInstructor(id);
                ins.setCertifications(certificationDao.getCertifications(id));
                ins.setActivities(activityCertificationDao.getAuthorizations(id));
                model.addAttribute("user", userDao.getUser(id));
                model.addAttribute("instructor", ins);
                return "instructor/profile";
            } else {
                return "redirect:/";
            }
        } else {
            return "redirect:/";
        }
    }

    //Veure solicitud monitor
    @RequestMapping("/request/{id}")
    public String seeInstructorRequest(HttpSession session, Model model, @PathVariable int id) {
        int acceso = controlarAcceso(session, "Admin");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/instructor/request/" + id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            Users user = userDao.getUser(id);
            if (user.getRol().equals("Request")) {
                Instructor ins = instructorDao.getInstructor(id);
                ins.setCertifications(certificationDao.getCertifications(id));
                model.addAttribute("user", userDao.getUser(id));
                model.addAttribute("instructor", ins);
                return "instructor/request";
            } else {
                return "redirect:/";
            }
        } else {
            return "redirect:/";
        }
    }

    //Acceptar solicitud monitor
    @RequestMapping("/accept/{id}")
    public String acceptInstructorRequest(HttpSession session, Model model, @PathVariable int id) {
        int acceso = controlarAcceso(session, "Admin");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/instructor/accept/" + id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            Users user = userDao.getUser(id);
            if (user.getRol().equals("Request")) {
                userDao.updateRole(id, "Instructor");
                return "redirect:/instructor/listRequests";
            } else {
                return "redirect:/";
            }
        } else {
            return "redirect:/";
        }
    }

    //Rebutjar solicitud monitor
    @RequestMapping("/reject/{id}")
    public String rejectInstructorRequest(HttpSession session, Model model, @PathVariable int id) {
        int acceso = controlarAcceso(session, "Admin");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/instructor/reject/" + id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            Users user = userDao.getUser(id);
            if (user.getRol().equals("Request")) {
                userDao.updateRole(id, "Rejected");
                return "redirect:/instructor/listRequests";
            } else {
                return "redirect:/";
            }
        } else {
            return "redirect:/";
        }
    }

    //Assignar activitat a monitor
    @RequestMapping(value="/asignarActivitat/{id}", method= RequestMethod.GET)
    public String asignarActivitats(HttpSession session, Model model, @PathVariable int id){
        int acceso = controlarAcceso(session, "Admin");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/instructor/asignarActivitat/" + id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            Users user = userDao.getUser(id);
            if (user.getRol().equals("Instructor")) {
                List<Certification> certifications = certificationDao.getCertifications(id);
                List<ActivityType> todas = activityTypeDao.getActivityTypes();
                List<ActivityType> asignadas = activityCertificationDao.getAuthorizations(id);
                todas.removeAll(asignadas);
                model.addAttribute("user", userDao.getUser(id));
                model.addAttribute("certifications", certifications);
                model.addAttribute("activities", todas);
                model.addAttribute("authorization", new ActivityCertification());
                return "instructor/asignarActivitat";
            } else {
                return "redirect:/";
            }
        } else {
            return "redirect:/";
        }
    }

    //Processa la informació del assignar activitat
    @RequestMapping(value="/asignarActivitat/{id}", method = RequestMethod.POST)
    public String processAsignarSubmit(Model model, @PathVariable int id, @ModelAttribute("user") Users user,
                                       @ModelAttribute("authorization") ActivityCertification authorization, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "instructor/asignarActivitat";
        }
        activityCertificationDao.addActivityCertification(authorization);

        return "redirect:/instructor/profile/" + id;
    }


    /*PART QUE UTILITZA UN INSTRUCTOR*/

    //Afegir un nou instructor
    @RequestMapping("/add")
    public String addInstructor(Model model) {
        model.addAttribute("user", new Users());
        model.addAttribute("instructor", new Instructor());
        model.addAttribute("certificationNames", new CertificationNames());
        return "instructor/add";
    }

    //Processa la informació del add
    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(HttpSession session, @RequestParam("foto") MultipartFile foto,
                                   @ModelAttribute("user") Users user, @ModelAttribute("instructor") Instructor instructor,
                                   @ModelAttribute("certificationNames") CertificationNames names, @RequestParam("cert1") MultipartFile cert1,
                                   @RequestParam("cert2") MultipartFile cert2, @RequestParam("cert3") MultipartFile cert3,
                                   @RequestParam("cert4") MultipartFile cert4, @RequestParam("cert5") MultipartFile cert5,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "instructor/add";

        if (foto.isEmpty()) {
            // Enviar mensaje de error porque no hay fichero seleccionado
            return "instructor/add";
        }

        if (cert1.isEmpty()) {
            // Enviar mensaje de error porque no hay fichero seleccionado
            return "instructor/add";
        }

        user.setRol("Request");
        user.setPasswd(passwordEncryptor.encryptPassword(user.getPasswd()));

        userDao.addUser(user);
        Users newUser = userDao.getUser(user.getUsername());

        session.setAttribute("user", newUser);

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

        saveCertificate(cert1, newUser.getId(), names.getCertificate1(), 1);
        if (!cert2.isEmpty()) {
            saveCertificate(cert2, newUser.getId(), names.getCertificate2(), 2);
        }
        if (!cert3.isEmpty()) {
            saveCertificate(cert3, newUser.getId(), names.getCertificate3(), 3);
        }
        if (!cert4.isEmpty()) {
            saveCertificate(cert4, newUser.getId(), names.getCertificate4(), 4);
        }
        if (!cert5.isEmpty()) {
            saveCertificate(cert5, newUser.getId(), names.getCertificate5(), 5);
        }

        return "redirect:../";
    }

    private void saveCertificate(MultipartFile cert, int id, String name, int number) {
        try {
            // Obtener el fichero y guardarlo
            byte[] bytes = cert.getBytes();

            String direccion = "certificates/" + id + "_" + number + ".pdf";

            Path path = Paths.get(uploadDirectory + direccion);

            Certification certification = new Certification();
            certification.setCertificate(name);
            certification.setDoc("/" + direccion);
            certification.setIdInstructor(id);
            System.out.println(name);

            certificationDao.addCertification(certification);

            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
