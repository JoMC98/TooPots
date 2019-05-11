package es.uji.ei1027.toopots.controller;

import es.uji.ei1027.toopots.daos.*;
import es.uji.ei1027.toopots.exceptions.TooPotsException;
import es.uji.ei1027.toopots.model.*;
import es.uji.ei1027.toopots.validator.ActivityCertificationValidator;
import es.uji.ei1027.toopots.validator.CertificationNamesValidator;
import es.uji.ei1027.toopots.validator.InstructorValidator;
import es.uji.ei1027.toopots.validator.UserValidator;
import org.apache.commons.io.FilenameUtils;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
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
    private CertificationDao certificationDao;
    private ReservationDao reservationDao;
    private ActivityPhotosDao activityPhotosDao;
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
    public void setActivityPhotosDao(ActivityPhotosDao activityPhotosDao) {
        this.activityPhotosDao=activityPhotosDao;
    }

    @Autowired
    public void setReservationDao(ReservationDao reservationDao) {
        this.reservationDao=reservationDao;
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

    //Afegir un nou instructor
    @RequestMapping("/add")
    public String addInstructor(Model model) {
        model.addAttribute("user", new Users());
        model.addAttribute("instructor", new Instructor());
        model.addAttribute("certificationNames", new CertificationNames());
        model.addAttribute("errorFoto", false);
        model.addAttribute("errorCert", false);
        return "instructor/add";
    }

    //Processa la informació del add
    @RequestMapping(value="/add", method= RequestMethod.POST)
    public String processAddSubmit(Model model, HttpSession session, @RequestParam("foto") MultipartFile foto,
                                   @ModelAttribute("user") Users user, BindingResult bindingResultUser,
                                   @ModelAttribute("instructor") Instructor instructor, BindingResult bindingResultInstructor,
                                   @ModelAttribute("certificationNames") CertificationNames certificationNames,
                                   BindingResult bindingResultNames, @RequestParam("cert") MultipartFile[] cert,
                                   BindingResult bindingResult) {

        InstructorValidator instructorValidator = new InstructorValidator();
        instructorValidator.validate(instructor, bindingResultInstructor);

        UserValidator userValidator = new UserValidator();
        userValidator.validate(user,bindingResultUser);

        CertificationNamesValidator certificationNamesValidator = new CertificationNamesValidator();
        certificationNamesValidator.setNumbFiles(cert.length);
        certificationNamesValidator.validate(certificationNames, bindingResultNames);

        if (foto.isEmpty()) {
            model.addAttribute("errorFoto", true);
        }

        if (cert[0].getSize() == 0) {
            model.addAttribute("errorCert", true);
        }

        if (bindingResultInstructor.hasErrors() || bindingResultUser.hasErrors() || bindingResultNames.hasErrors() ||
                foto.isEmpty() || cert[0].getSize() == 0) {
            System.out.println(bindingResultInstructor.toString());
            System.out.println(bindingResultUser.toString());

            return "instructor/add";
        }

        user.setRol("Request");
        user.setPasswd(passwordEncryptor.encryptPassword(user.getPasswd()));

        try {
            userDao.addUser(user);
        } catch (DuplicateKeyException e) {
            throw new TooPotsException(
                    "El nom d'usuari ja esta en ús", "Prova amb un altre",
                    "ClauPrimariaDuplicada");
        }
        Users newUser = userDao.getUser(user.getUsername());

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

        List<String> names = new ArrayList<String>();
        names.add(certificationNames.getCertificate1());
        names.add(certificationNames.getCertificate2());
        names.add(certificationNames.getCertificate3());
        names.add(certificationNames.getCertificate4());
        names.add(certificationNames.getCertificate5());

        for (int i=0; i<cert.length; i++) {
            MultipartFile file = cert[i];
            saveCertificate(file, newUser.getId(), names.get(i), i+1);
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

            certificationDao.addCertification(certification);

            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Actualitzar un instructor
    @RequestMapping(value="/update", method = RequestMethod.GET)
    public String editInstructor(Model model, HttpSession session) {
        int acceso = controlarAcceso(session, "Instructor");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/instructor/update");
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            Users user = (Users) session.getAttribute("user");
            model.addAttribute("user", userDao.getUser(user.getId()));
            model.addAttribute("instructor", instructorDao.getInstructor(user.getId()));
            return "instructor/update";
        } else {
            return "redirect:/";
        }

    }

    //Processa la informació del update
    @RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("user") Users user,
                                      @ModelAttribute("instructor") Instructor instructor, BindingResult bindingResult) {

        if (bindingResult.hasErrors())
            return "instructor/update";

        userDao.updateUser(user);
        instructorDao.updateInstructor(instructor);
        return "redirect:../list";
    }

    //Llistar totes les activitats del monitor
    @RequestMapping("/listActivities")
    public String listActivities(Model model, HttpSession session) {
        int acceso = controlarAcceso(session, "Instructor");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/instructor/listActivities");
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            Users user = (Users) session.getAttribute("user");

            List<Activity> activities = activityDao.getActivities(user.getId());

            List<Activity> activitiesWithOcupation = new ArrayList<Activity>();
            for (Activity ac: activities) {
                ActivityPhotos photoPrincipal = activityPhotosDao.getPhotoPrincipal(ac.getId());
                ac.setPhotoPrincipal(photoPrincipal.getPhoto());

                List<Reservation> reservations = reservationDao.getReserves(ac.getId());
                float total = 0;
                for (Reservation res: reservations) {
                    total += res.getNumPeople();
                }

                total = (total/ac.getMaxNumberPeople())*100;
                total = (float) Math.floor(total);
                int ocupation = (int) total;
                ac.setOcupation(ocupation);

                activitiesWithOcupation.add(ac);
            }
            model.addAttribute("activities", activitiesWithOcupation);
            return "instructor/listActivities";
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
