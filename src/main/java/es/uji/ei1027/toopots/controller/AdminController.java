package es.uji.ei1027.toopots.controller;

import es.uji.ei1027.toopots.daos.*;
import es.uji.ei1027.toopots.model.*;
import es.uji.ei1027.toopots.validator.ActivityCertificationValidator;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final static int NOT_LOGGED = 1;
    private final static int USER_AUTHORIZED = 2;
    private final static int USER_NOT_AUTHORIZED = 3;
    private InstructorDao instructorDao;
    private UsersDao userDao;
    private ActivityTypeDao activityTypeDao;
    private ActivityDao activityDao;
    private CertificationDao certificationDao;
    private ActivityCertificationDao activityCertificationDao;

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
    public void setActivityTypeDao(ActivityTypeDao activityTypeDao){this.activityTypeDao=activityTypeDao;}

    @Autowired
    public void setActivityDao(ActivityDao activityDao){this.activityDao=activityDao;}

    @Autowired
    public void setCertificationDao(CertificationDao certificationDao){this.certificationDao=certificationDao;}

    @Autowired
    public void setActivityCertificationDao(ActivityCertificationDao activityCertificationDao){this.activityCertificationDao=activityCertificationDao;}

    //Admin home
    @RequestMapping("/home")
    public String home(HttpSession session, Model model) {
        int acceso = controlarAcceso(session, "Admin");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/admin/home");
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            List<Users> users = userDao.getRequests();
            model.addAttribute("total", users.size());
            return "admin/home";
        } else {
            return "redirect:/";
        }
    }

    //Llistar tots els instructors
    @RequestMapping("/listInstructors")
    public String listInstructors(HttpSession session, Model model) {
        int acceso = controlarAcceso(session, "Admin");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/admin/listInstructors");
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
            return "admin/listInstructors";
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
            session.setAttribute("nextUrl", "/admin/listRequests");
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
            return "admin/listRequests";
        } else {
            return "redirect:/";
        }
    }

    //Acceptar solicitud monitor
    @RequestMapping("/accept/{id}")
    public String acceptInstructorRequest(HttpSession session, Model model, @PathVariable int id) {
        Users user = userDao.getUser(id);
        if (user == null) {
            return "redirect:/";
        }
        int acceso = controlarAcceso(session, "Admin");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/admin/accept/" + id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            if (user.getRol().equals("Request")) {
                userDao.updateRole(id, "Instructor");
                return "redirect:/admin/listRequests";
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
        Users user = userDao.getUser(id);
        if (user == null) {
            return "redirect:/";
        }
        int acceso = controlarAcceso(session, "Admin");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/admin/reject/" + id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            if (user.getRol().equals("Request")) {
                userDao.updateRole(id, "Rejected");
                return "redirect:/admin/listRequests";
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
        Users user = userDao.getUser(id);
        if (user == null) {
            return "redirect:/";
        }
        int acceso = controlarAcceso(session, "Admin");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/admin/asignarActivitat/" + id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            if (user.getRol().equals("Instructor")) {
                List<Certification> certifications = certificationDao.getCertifications(id);
                List<ActivityType> todas = activityTypeDao.getActivityTypes();
                List<ActivityType> asignadas = activityCertificationDao.getAuthorizations(id);
                todas.removeAll(asignadas);

                Instructor instructor = instructorDao.getInstructor(id);
                instructor.setName(user.getName());
                instructor.setCertifications(certifications);
                instructor.setActivities(todas);

                model.addAttribute("instructor", instructor);
                model.addAttribute("authorization", new ActivityCertification());
                return "admin/asignarActivitat";
            } else {
                return "redirect:/";
            }
        } else {
            return "redirect:/";
        }
    }

    //Processa la informació del assignar activitat
    @RequestMapping(value="/asignarActivitat/{id}", method = RequestMethod.POST)
    public String processAsignarSubmit(HttpSession session, Model model, @PathVariable int id, @ModelAttribute("instructor") Instructor instructor,
                                       @ModelAttribute("authorization") ActivityCertification authorization,
                                       BindingResult bindingResult) {

        int acceso = controlarAcceso(session, "Admin");
        if (acceso == USER_AUTHORIZED) {
            ActivityCertificationValidator activityCertificationValidator = new ActivityCertificationValidator();
            activityCertificationValidator.validate(authorization, bindingResult);

            if (bindingResult.hasErrors()) {
                List<Certification> certifications = certificationDao.getCertifications(id);
                List<ActivityType> todas = activityTypeDao.getActivityTypes();
                List<ActivityType> asignadas = activityCertificationDao.getAuthorizations(id);
                todas.removeAll(asignadas);

                Users user = userDao.getUser(id);
                instructor.setName(user.getName());
                instructor.setCertifications(certifications);
                instructor.setActivities(todas);

                model.addAttribute("instructor", instructor);
                return "admin/asignarActivitat";
            }
            activityCertificationDao.addActivityCertification(authorization);

            return "redirect:/admin/instructorProfile/" + id;
        } else {
            return "redirect:/admin/asignarActivitat/" + id;
        }
    }

    //Borrar asignació d'activitat del monitor
    @RequestMapping(value="/borrarAsignacion/{id}", method= RequestMethod.POST)
    public String borrarAsignacio(HttpSession session, Model model, @PathVariable int id,
                                  @ModelAttribute("instructor") Instructor instructor) {

        ActivityType activityType = activityTypeDao.getActivityType(id);
        if (activityType == null) {
            return "redirect:/";
        }
        int acceso = controlarAcceso(session, "Admin");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/admin/borrarAsignacion/" + id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            Users user = userDao.getUser(instructor.getId());
            if (user.getRol().equals("Instructor")) {
                Instructor ins = instructorDao.getInstructor(instructor.getId());
                ins.setCertifications(certificationDao.getCertifications(instructor.getId()));
                ins.setActivities(activityCertificationDao.getAuthorizations(instructor.getId()));

                model.addAttribute("user", userDao.getUser(instructor.getId()));
                model.addAttribute("instructor", ins);
                model.addAttribute("borrarAsignacio", true);
                model.addAttribute("activityBorrarAsignacio", activityType.getId());
                return "admin/instructorProfile";
            } else {
                return "redirect:/";
            }
        } else {
            return "redirect:/";
        }
    }

    //Para que no falle el get
    @RequestMapping(value="/borrarAsignacion/{id}", method= RequestMethod.GET)
    public String borrarAsignacioGet (@PathVariable int id) {
        return "redirect:/home";
    }

    //Borrar asignació d'activitat del monitor
    @RequestMapping(value="/confirmarBorradoAsignacion/{id}", method= RequestMethod.POST)
    public String confirmBorrarAsignacio(HttpSession session, Model model, @PathVariable int id,
                                  @ModelAttribute("instructor") Instructor instructor){

        Users user = userDao.getUser(instructor.getId());
        ActivityType activityType = activityTypeDao.getActivityType(id);
        if (user == null || activityType == null) {
            return "redirect:/admin/instructorProfile/" + instructor.getId();
        }

        List<ActivityType> authorizations = activityCertificationDao.getAuthorizations(instructor.getId());

        if (! authorizations.contains(activityType)) {
            return "redirect:/admin/instructorProfile/" + instructor.getId();
        }

        int acceso = controlarAcceso(session, "Admin");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/admin/borrarAsignacio/" + instructor.getId() + "/" + id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            if (user.getRol().equals("Instructor")) {
                List<Certification> certifications = certificationDao.getCertifications(instructor.getId());
                for (Certification c: certifications) {
                    activityCertificationDao.deleteAuthorization(c.getId(), id);
                }
                activityDao.cancelAllActivitiesFromThisType(instructor.getId(), activityType);
                return "redirect:/admin/instructorProfile/" + instructor.getId();
            } else {
                return "redirect:/";
            }
        } else {
            return "redirect:/";
        }
    }

    //Para que no falle el get
    @RequestMapping(value="/confirmarBorradoAsignacion/{id}", method= RequestMethod.GET)
    public String confirmBorrarAsignacioGet (@PathVariable int id) {
        return "redirect:/home";
    }


    //Veure perfil monitor
    @RequestMapping("/instructorProfile/{id}")
    public String seeInstructor(HttpSession session, Model model, @PathVariable int id) {
        Users user = userDao.getUser(id);
        if (user == null) {
            return "redirect:/";
        }
        int acceso = controlarAcceso(session, "Admin");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/admin/instructorProfile/" + id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            if (user.getRol().equals("Instructor")) {
                Instructor ins = instructorDao.getInstructor(id);
                ins.setCertifications(certificationDao.getCertifications(id));
                ins.setActivities(activityCertificationDao.getAuthorizations(id));

                model.addAttribute("user", userDao.getUser(id));
                model.addAttribute("instructor", ins);
                return "admin/instructorProfile";
            } else {
                return "redirect:/";
            }
        } else {
            return "redirect:/";
        }
    }

    //Veure solicitud monitor
    @RequestMapping("/instructorRequest/{id}")
    public String seeInstructorRequest(HttpSession session, Model model, @PathVariable int id) {
        Users user = userDao.getUser(id);
        if (user == null) {
            return "redirect:/";
        }
        int acceso = controlarAcceso(session, "Admin");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/admin/instructorRequest/" + id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            if (user.getRol().equals("Request")) {
                Instructor ins = instructorDao.getInstructor(id);
                ins.setCertifications(certificationDao.getCertifications(id));
                model.addAttribute("user", userDao.getUser(id));
                model.addAttribute("instructor", ins);
                return "admin/instructorRequest";
            } else {
                return "redirect:/";
            }
        } else {
            return "redirect:/";
        }
    }

    //Donar de baixa monitor
    @RequestMapping("/delete/{id}")
    public String deleteInstructor(HttpSession session, Model model, @PathVariable int id) {
        Users user = userDao.getUser(id);
        if (user == null) {
            return "redirect:/";
        }
        int acceso = controlarAcceso(session, "Admin");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/admin/delete/" + id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            if (user.getRol().equals("Instructor")) {
                List<Activity> listaObertes = activityDao.getActivities(id, "Oberta");
                List<Activity> listaTancades = activityDao.getActivities(id, "Tancada");

                if (listaObertes.size() != 0 || listaTancades.size() != 0) {
                    model.addAttribute("instructorWithActivities", true);

                } else {
                    model.addAttribute("instructorWithoutActivities", true);
                }

                Instructor ins = instructorDao.getInstructor(id);
                ins.setCertifications(certificationDao.getCertifications(id));
                ins.setActivities(activityCertificationDao.getAuthorizations(id));
                model.addAttribute("user", userDao.getUser(id));

                model.addAttribute("instructor", ins);
                return "admin/instructorProfile";
            } else {
                return "redirect:/";
            }
        } else {
            return "redirect:/";
        }
    }

    //Confirmar baixa monitor
    @RequestMapping("/confirmDeletion/{id}")
    public String confirmDeletionInstructor(HttpSession session, Model model, @PathVariable int id) {
        Users user = userDao.getUser(id);
        if (user == null) {
            return "redirect:/";
        }
        int acceso = controlarAcceso(session, "Admin");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/admin/delete/" + id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            if (user.getRol().equals("Instructor")) {
                userDao.updateRole(id, "Fired");
                activityDao.cancelAllActivities(id, "Monitor donat de baixa per el administrador");
            }
        }
        return "redirect:/";
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
