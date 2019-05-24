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
import java.io.File;
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
    public void setActivityPhotosDao(ActivityPhotosDao activityPhotosDao) {
        this.activityPhotosDao=activityPhotosDao;
    }

    @Autowired
    public void setReservationDao(ReservationDao reservationDao) {
        this.reservationDao=reservationDao;
    }

    @Autowired
    public void setActivityCertificationDao(ActivityCertificationDao activityCertificationDao){this.activityCertificationDao=activityCertificationDao;}


    /*Part utilitzada pel instructor*/

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
                                   BindingResult bindingResultNames, @RequestParam("cert") MultipartFile[] cert) {

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

        for (int i=0; i<cert.length; i++) {
            MultipartFile file = cert[i];
            saveCertificate(file, newUser.getId(), names.get(i), i+1);
        }
        return "redirect:../";
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
            Instructor ins = instructorDao.getInstructor(user.getId());
            model.addAttribute("user", userDao.getUser(user.getId()));
            model.addAttribute("instructor", ins);
            model.addAttribute("certifications", certificationDao.getCertifications(user.getId()));
            model.addAttribute("certificationNames", new CertificationNames());
            model.addAttribute("errorFoto", false);
            return "instructor/update";
        } else {
            return "redirect:/";
        }

    }

    //Processa la informació del update
    @RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(Model model, HttpSession session, @ModelAttribute("user") Users user, BindingResult bindingResultUser,
                                      @ModelAttribute("instructor") Instructor instructor, BindingResult bindingResultInstructor,
                                      @ModelAttribute("certificationNames") CertificationNames certificationNames, BindingResult bindingResultNames,
                                      @RequestParam("foto") MultipartFile foto, @RequestParam("cert") MultipartFile cert) {

        Users viejo = (Users) session.getAttribute("user");
        user.setId(viejo.getId());

        CertificationNamesValidator certificationNamesValidator = new CertificationNamesValidator();
        if (!cert.isEmpty()) {
            certificationNamesValidator.setNumbFiles(1);
            certificationNamesValidator.validate(certificationNames, bindingResultNames);
        }

        InstructorValidator instructorValidator = new InstructorValidator();
        instructorValidator.validate(instructor, bindingResultInstructor);

        UserValidator userValidator = new UserValidator();
        userValidator.validate(user,bindingResultUser);

        if (bindingResultNames.hasErrors()) {
            model.addAttribute("errorCert", true);
        }

        if (bindingResultInstructor.hasErrors() || bindingResultUser.hasErrors() || bindingResultNames.hasErrors()) {
            instructor.setPhoto(instructorDao.getInstructor(user.getId()).getPhoto());
            model.addAttribute("certifications", certificationDao.getCertifications(user.getId()));
            System.out.println(bindingResultInstructor.toString());
            System.out.println(bindingResultUser.toString());
            System.out.println(bindingResultNames.toString());
            return "instructor/update";
        }

        if (!foto.isEmpty()) {
            try {
                // Obtener el fichero y guardarlo
                byte[] bytes = foto.getBytes();

                String extension = FilenameUtils.getExtension(foto.getOriginalFilename());
                String direccion = "images/instructorProfiles/" + user.getId() + "." + extension;

                Path path = Paths.get(uploadDirectory + direccion);

                borrarFotoActualizada(uploadDirectory + instructor.getPhoto());
                instructor.setPhoto("/" + direccion);

                Files.write(path, bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!cert.isEmpty()) {
            List<Certification> certifications= certificationDao.getCertifications(user.getId());
            saveCertificate(cert, user.getId(), certificationNames.getCertificate1(), certifications.size() + 1);
        }


        instructor.setId(user.getId());
        userDao.updateUser(user);
        instructorDao.updateInstructor(instructor);
        return "redirect:/";
    }

    //Llistat de totes les activitats del monitor per a ell
    @RequestMapping("/listActivities/{state}")
    public String listActivities(Model model, HttpSession session, @PathVariable String state) {
        int acceso = controlarAcceso(session, "Instructor");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/instructor/listActivities/" + state);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            Users user = (Users) session.getAttribute("user");

            if (!state.equals("opened") && !state.equals("closed") && !state.equals("canceled") && !state.equals("done")) {
                return "redirect:/";
            }

            List<Activity> activities = activityDao.getActivities(user.getId(), state);

            List<Activity> activitiesWithOcupation = new ArrayList<Activity>();
            for (Activity ac: activities) {

                List<ActivityPhotos> imatges = activityPhotosDao.getPhotos(ac.getId());
                if (imatges.size() == 1) {
                    ac.setPhotoPrincipal(imatges.get(0).getPhoto());
                } else {
                    ac.setImages(imatges);
                }
                ac.setTotalImages(imatges.size());

                List<Reservation> reservations = reservationDao.getReservesFromActivity(ac.getId());
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
            model.addAttribute("estat", state);
            return "instructor/listActivities";
        } else {
            return "redirect:/";
        }
    }




    /*Part utilitzada pels clients*/

    //Llistat de totes les activitats del monitor per als clients
    @RequestMapping("/showActivities/{state}/{id}")
    public String activityListForCustomers(Model model, @PathVariable int id, @PathVariable String state) {
        Users user = userDao.getUser(id);
        if (user == null) {
            return "redirect:/";
        }

        if (user.getRol().equals("Instructor")) {
            if (!state.equals("opened") && !state.equals("closed") && !state.equals("done")) {
                return "redirect:/";
            }
            List<Activity> activities = activityDao.getActivities(user.getId(), state);

            List<Activity> activitiesWithOcupation = new ArrayList<Activity>();
            for (Activity ac : activities) {

                List<ActivityPhotos> imatges = activityPhotosDao.getPhotos(ac.getId());
                if (imatges.size() == 1) {
                    ac.setPhotoPrincipal(imatges.get(0).getPhoto());
                } else {
                    ac.setImages(imatges);
                }
                ac.setTotalImages(imatges.size());

                List<Reservation> reservations = reservationDao.getReservesFromActivity(ac.getId());
                float total = 0;
                for (Reservation res : reservations) {
                    total += res.getNumPeople();
                }

                total = (total / ac.getMaxNumberPeople()) * 100;
                total = (float) Math.floor(total);
                int ocupation = (int) total;
                ac.setOcupation(ocupation);

                activitiesWithOcupation.add(ac);
            }
            model.addAttribute("user", user);
            model.addAttribute("activities", activitiesWithOcupation);
            model.addAttribute("estat", state);
            return "instructor/showActivities";
        } else {
            return "redirect:/";
        }
    }

    //Veure perfil monitor
    @RequestMapping("/profile/{id}")
    public String seeInstructor(Model model, @PathVariable int id) {
        Users user = userDao.getUser(id);
        if (user == null) {
            return "redirect:/";
        }

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

    private void borrarFotoActualizada(String nombre) {
        File fichero = new File(nombre);
        if (fichero.exists()) {
            fichero.delete();
        }
    }
}
