package es.uji.ei1027.toopots.controller;

import es.uji.ei1027.toopots.comparator.*;
import es.uji.ei1027.toopots.daos.*;
import es.uji.ei1027.toopots.exceptions.TooPotsException;
import es.uji.ei1027.toopots.model.*;
import es.uji.ei1027.toopots.validator.ActivityCancelationValidator;
import es.uji.ei1027.toopots.validator.ActivityValidator;
import es.uji.ei1027.toopots.validator.ReservationValidator;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Controller
@RequestMapping("/activity")
public class ActivityController {
    private final static int NOT_LOGGED = 1;
    private final static int USER_AUTHORIZED = 2;
    private final static int USER_NOT_AUTHORIZED = 3;

    private ActivityDao activityDao;
    private ActivityTypeDao activityTypeDao;
    private ActivityRatesDao activityRatesDao;
    private ActivityPhotosDao activityPhotosDao;
    private InstructorDao instructorDao;
    private ReservationDao reservationDao;
    private ActivityCertificationDao activityCertificationDao;
    private UsersDao usersDao;

    @Value("${upload.file.directory}")
    private String uploadDirectory;

    @Autowired
    public void setActivityDao(ActivityDao activityDao) {
        this.activityDao=activityDao;
    }

    @Autowired
    public void setInstructorDao(InstructorDao instructorDao) {
        this.instructorDao=instructorDao;
    }

    @Autowired
    public void setActivityTypeDao(ActivityTypeDao activityTypeDao) {
        this.activityTypeDao=activityTypeDao;
    }

    @Autowired
    public void setActivityRatesDao(ActivityRatesDao activityRatesDao) {
        this.activityRatesDao=activityRatesDao;
    }

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

    @Autowired
    public void setUsersDao(UsersDao userDao) {
        this.usersDao = userDao;
    }


    /*Part utilitzada pel instructor*/

    //Afegir una nova activitat
    @RequestMapping("/add")
    public String addActivity(HttpSession session, Model model) {
        int acceso = controlarAcceso(session, "Instructor");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/activity/add");
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            Users user = (Users) session.getAttribute("user");
            List<ActivityType> asignadas = activityCertificationDao.getAuthorizations(user.getId());
            Instructor instructor = instructorDao.getInstructor(user.getId());
            instructor.setActivities(asignadas);
            model.addAttribute("activity", new Activity());
            model.addAttribute("instructor", instructor);
            model.addAttribute("errorFoto", false);
            return "activity/add";
        } else {
            return "redirect:/";
        }
    }

    //Processa la informació del add
    @RequestMapping(value="/add", method=RequestMethod.POST)
    public String processAddSubmit(Model model, HttpSession session, @ModelAttribute("activity") Activity activity,
                                   BindingResult bindingResult, @RequestParam("foto") MultipartFile[] foto) {
        int acceso = controlarAcceso(session, "Instructor");
        if (acceso == USER_AUTHORIZED) {
            if (foto[0].getSize() == 0) {
                model.addAttribute("errorFoto", true);
            }

            boolean errorTarifas = false;

            float porPersona = activity.getPricePerPerson();

            if (activity.getTarifaMenores().getPrice() >= porPersona ||
                activity.getTarifaEstudiantes().getPrice() >= porPersona ||
                activity.getTarifaMayores().getPrice() >= porPersona ||
                activity.getTarifaGrupos().getPrice() >= porPersona) {
                errorTarifas = true;
            }

            ActivityValidator activityValidator = new ActivityValidator();
            activityValidator.validate(activity, bindingResult);

            if (bindingResult.hasErrors() || foto[0].getSize() == 0 || errorTarifas) {
                Users user = (Users) session.getAttribute("user");
                List<ActivityType> asignadas = activityCertificationDao.getAuthorizations(user.getId());
                Instructor instructor = instructorDao.getInstructor(user.getId());
                instructor.setActivities(asignadas);
                model.addAttribute("instructor", instructor);
                model.addAttribute("errorTarifas", errorTarifas);
                return "activity/add";
            }

            Users user = (Users) session.getAttribute("user");
            activity.setIdInstructor(user.getId());

            try {
                activityDao.addActivity(activity);
            } catch (DuplicateKeyException e) {
                throw new TooPotsException(
                        "Ja tens una activitat per al dia " + activity.getDates().getDayOfMonth() + " del "
                                + activity.getDates().getMonthValue() + " de " + activity.getDates().getYear(),
                        "Prova un altra data",
                        "ClauPrimariaDuplicada");
            }


            Activity newActivity = activityDao.getActivity(activity.getDates(), activity.getIdInstructor());
            int id = newActivity.getId();

            for (int i = 0; i < foto.length; i++) {
                MultipartFile file = foto[i];
                saveFoto(file, newActivity, i + 1);
            }

            if (activity.getTarifaMenores().getPrice() > 0.0) {
                ActivityRates tarifa = activity.getTarifaMenores();
                tarifa.setIdActivity(id);
                activityRatesDao.addActivityRates(tarifa);
            }

            if (activity.getTarifaEstudiantes().getPrice() > 0.0) {
                ActivityRates tarifa = activity.getTarifaEstudiantes();
                tarifa.setIdActivity(id);
                activityRatesDao.addActivityRates(tarifa);
            }
            if (activity.getTarifaMayores().getPrice() > 0.0) {
                ActivityRates tarifa = activity.getTarifaMayores();
                tarifa.setIdActivity(id);
                activityRatesDao.addActivityRates(tarifa);
            }
            if (activity.getTarifaGrupos().getPrice() > 0.0) {
                ActivityRates tarifa = activity.getTarifaGrupos();
                tarifa.setIdActivity(id);
                activityRatesDao.addActivityRates(tarifa);
            }

            session.setAttribute("activityCreated", true);

            return "redirect:/";
        } else {
            return "redirect:/activity/add";
        }
    }


    //Actualitzar una activitat
    @RequestMapping(value="/update/{id}", method = RequestMethod.GET)
    public String editActivity(HttpSession session, Model model, @PathVariable int id) {
        Activity activity = activityDao.getActivity(id);
        if (activity == null) {
            return "redirect:/";
        }
        int acceso = controlarAccesoYId(session, "Instructor", activity.getIdInstructor());
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/activity/update/"+id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            if (activity.getState().equals("Oberta") || activity.getState().equals("Tancada")) {
                Users user = (Users) session.getAttribute("user");
                List<ActivityType> asignadas = activityCertificationDao.getAuthorizations(user.getId());
                List<ActivityRates> tarifas = activityRatesDao.getActivityRates(id);

                Instructor instructor = instructorDao.getInstructor(user.getId());
                instructor.setActivities(asignadas);
                List<ActivityPhotos> photos = activityPhotosDao.getPhotos(id);

                for (int i = photos.size(); i<4; i++) {
                    photos.add(null);
                }

                for (ActivityRates tarifa: tarifas) {
                    if(tarifa.getRateName().equals("Menors de 16 anys")) {
                        activity.setTarifaMenores(tarifa);
                    }
                    else if(tarifa.getRateName().equals("Estudiants")) {
                        activity.setTarifaEstudiantes(tarifa);
                    }
                    else if(tarifa.getRateName().equals("Majors de 60 anys")) {
                        activity.setTarifaMayores(tarifa);
                    } else {
                        activity.setTarifaGrupos(tarifa);
                    }
                }

                model.addAttribute("activity", activity);
                model.addAttribute("photos", photos);
                model.addAttribute("instructor", instructor);
                return "activity/update";
            } else {
                return "redirect:/";
            }
        } else {
            return "redirect:/";
        }
    }

    //Processa la informació del update
    @RequestMapping(value="/update/{id}", method = RequestMethod.POST)
    public String processUpdateSubmit(Model model, HttpSession session, @PathVariable int id, @ModelAttribute("activity") Activity activity,
                                      @RequestParam("foto0") MultipartFile foto0, @RequestParam("foto1") MultipartFile foto1,
                                      @RequestParam("foto2") MultipartFile foto2, @RequestParam("foto3") MultipartFile foto3,
                                      BindingResult bindingResult) {

        int acceso = controlarAcceso(session, "Instructor");
        if (acceso == USER_AUTHORIZED) {
            ActivityValidator activityValidator = new ActivityValidator();
            activityValidator.validate(activity, bindingResult);

            if (bindingResult.hasErrors()) {
                Users user = (Users) session.getAttribute("user");
                List<ActivityType> asignadas = activityCertificationDao.getAuthorizations(user.getId());
                Instructor instructor = instructorDao.getInstructor(user.getId());
                instructor.setActivities(asignadas);
                model.addAttribute("instructor", instructor);
                return "activity/update";
            }

            Users user = (Users) session.getAttribute("user");
            activity.setIdInstructor(user.getId());

            List<ActivityRates> tarifasOld = activityRatesDao.getActivityRates(id);
            Activity activityOld = activityDao.getActivity(id);

            for (ActivityRates tarifa: tarifasOld) {
                if (tarifa.getRateName().equals("Menors de 16 anys")) {
                    activityOld.setTarifaMenores(tarifa);
                } else if (tarifa.getRateName().equals("Estudiants")) {
                    activityOld.setTarifaEstudiantes(tarifa);
                } else if (tarifa.getRateName().equals("Majors de 60 anys")) {
                    activityOld.setTarifaMayores(tarifa);
                } else {
                    activityOld.setTarifaGrupos(tarifa);
                }
            }

            List<ActivityRates> vieja = new LinkedList<ActivityRates>();
            vieja.add(activityOld.getTarifaMenores());
            vieja.add(activityOld.getTarifaEstudiantes());
            vieja.add(activityOld.getTarifaMayores());
            vieja.add(activityOld.getTarifaGrupos());

            List<ActivityRates> nueva = new LinkedList<ActivityRates>();
            nueva.add(activity.getTarifaMenores());
            nueva.add(activity.getTarifaEstudiantes());
            nueva.add(activity.getTarifaMayores());
            nueva.add(activity.getTarifaGrupos());

            for (int i=0; i<4; i++) {
                ActivityRates tarifaNueva = nueva.get(i);
                ActivityRates tarifaVieja = vieja.get(i);
                tarifaNueva.setIdActivity(id);

                //Si la tarifa cambia
                if (! tarifaNueva.equals(tarifaVieja)) {
                    //Si ahora es 0, eliminar
                    if (tarifaNueva.getPrice() == 0) {
                        activityRatesDao.deleteActivityRates(tarifaNueva);
                    }
                    //Si la de antes era 0, añadir
                    else if (tarifaVieja.getPrice() == 0) {
                        activityRatesDao.addActivityRates(tarifaNueva);
                    }
                    //Sino modificar
                    else {
                        activityRatesDao.updateActivityRates(tarifaNueva);
                    }
                }
            }

            List<ActivityPhotos> photos = activityPhotosDao.getPhotos(activity.getId());

            MultipartFile[] inputFotos = {foto0, foto1, foto2, foto3};

            for (int i=0; i<4; i++) {
                int photoNumber = i + 1;
                if (!inputFotos[i].isEmpty()) {
                    try {
                        // Obtener el fichero y guardarlo
                        byte[] bytes = inputFotos[i].getBytes();

                        String extension = FilenameUtils.getExtension(inputFotos[i].getOriginalFilename());
                        String direccion = "images/activities/" + activity.getId() + "_" + photoNumber + "." + extension;

                        Path path = Paths.get(uploadDirectory + direccion);


                        //TODO BORRAR VIEJA
                        ActivityPhotos photo = activityPhotosDao.getActivityPhotos(activity.getId(), photoNumber);
                        if (photo == null) {
                            ActivityPhotos activityPhotos = new ActivityPhotos();
                            activityPhotos.setIdActivity(activity.getId());
                            activityPhotos.setPhotoNumber(photoNumber);
                            activityPhotos.setPhoto("/" + direccion);
                            activityPhotosDao.addActivityPhotos(activityPhotos);
                        } else {
                            borrarFotoActualizada(uploadDirectory + photo.getPhoto());
                            photo.setPhoto("/" + direccion);
                            activityPhotosDao.updateActivityPhotos(photo);
                        }
                        Files.write(path, bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                activityDao.updateActivity(activity);
            } catch (DuplicateKeyException e) {
                throw new TooPotsException(
                        "Ja tens una activitat per al dia " + activity.getDates().getDayOfMonth() + " del "
                                + activity.getDates().getMonthValue() + " de " + activity.getDates().getYear(),
                        "Prova un altra data",
                        "ClauPrimariaDuplicada");
            }
            session.setAttribute("activityUpdated", true);

            return "redirect:/";
        } else {
            return "redirect:/activity/update/" + id;
        }
    }



    //Controlar una cancelacio
    @RequestMapping("/cancel/{id}")
    public String cancelActivity(HttpSession session, Model model, @PathVariable int id) {
        Activity activity = activityDao.getActivity(id);
        if (activity == null) {
            return "redirect:/";
        }
        int acceso = controlarAccesoYId(session, "Instructor", activity.getIdInstructor());
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "activity/cancel/"+id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            if (activity.getState().equals("Oberta") || activity.getState().equals("Tancada")) {
                model.addAttribute("activity", activity);
                return "activity/cancel";
            } else {
                return "redirect:/";
            }
        } else {
            return "redirect:/";
        }

    }

    //Processa la informació de la cancelació
    @RequestMapping(value="/cancel/{id}", method = RequestMethod.POST)
    public String processCancelSubmit(HttpSession session, Model model, @PathVariable int id, @ModelAttribute("activity") Activity activity,
                                      BindingResult bindingResult) {

        Activity oldActivity = activityDao.getActivity(id);
        int acceso = controlarAccesoYId(session, "Instructor", oldActivity.getIdInstructor());
        if (acceso == USER_AUTHORIZED) {
            ActivityCancelationValidator cancelationValidator = new ActivityCancelationValidator();
            cancelationValidator.validate(activity, bindingResult);
            if (bindingResult.hasErrors()) {
                System.out.println(bindingResult.toString());
                return "activity/cancel";
            }
            activity.setState("Cancel·lada");
            activityDao.cancelActivity(activity);
            session.setAttribute("activityCanceled", true);
            return "redirect:/home";
        } else {
            return "redirect:/activity/cancel/" + id;
        }
    }

    //Tancar una activitat
    @RequestMapping("/close/{id}")
    public String closeActivity(HttpSession session, Model model, @PathVariable int id) {
        Activity activity = activityDao.getActivity(id);
        if (activity == null) {
            return "redirect:/";
        }
        int acceso = controlarAccesoYId(session, "Instructor", activity.getIdInstructor());
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "activity/close/"+id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            if (activity.getState().equals("Oberta") || activity.getState().equals("Tancada")) {
                activity.setState("Tancada");
                activityDao.updateActivityState(activity);
                session.setAttribute("activityClosed", true);
                return "redirect:/home";
            } else {
                return "redirect:/";
            }
        } else {
            return "redirect:/";
        }
    }

    //Obrir una activitat
    @RequestMapping("/open/{id}")
    public String openActivity(HttpSession session, Model model, @PathVariable int id) {
        Activity activity = activityDao.getActivity(id);
        if (activity == null) {
            return "redirect:/";
        }
        int acceso = controlarAccesoYId(session, "Instructor", activity.getIdInstructor());
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "activity/open/"+id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            if (activity.getState().equals("Oberta") || activity.getState().equals("Tancada")) {
                activity.setState("Oberta");
                activityDao.updateActivityState(activity);

                session.setAttribute("activityOpened", true);
                return "redirect:/home";
            } else {
                return "redirect:/";
            }
        } else {
            return "redirect:/";
        }
    }

    //Llistar totes les reserves d'una activitat
    @RequestMapping(value="/listReservations/{id}", method = RequestMethod.GET)
    public String listReservesActivity(HttpSession session, Model model, @PathVariable int id) {
        Activity activity = activityDao.getActivity(id);
        if (activity == null) {
            return "redirect:/";
        }
        int acceso = controlarAccesoYId(session, "Instructor", activity.getIdInstructor());
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "activity/listReservations/"+id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            List<Reservation> reserves = reservationDao.getReservesFromActivity(id);
            for (Reservation reserve: reserves){
                Users user = usersDao.getUser(reserve.getIdCustomer());
                reserve.setNameCustomer(user.getName());
            }
            model.addAttribute("reserves", reserves);
            return "activity/listReservations";
        } else {
            return "redirect:/";
        }
    }



    /*Part utilitzada pels clients*/

    //Oferta d'activitats
    @RequestMapping("/offer")
    public String offer(HttpSession session, Model model) {
        List<Activity> activities = activityDao.getActivities("Oberta");
        List<Activity> activitiesWithOcupation = new ArrayList<Activity>();
        for (Activity ac: activities) {
            List<Reservation> reservations = reservationDao.getReservesFromActivity(ac.getId());
            ActivityType activityType = activityTypeDao.getActivityType(ac.getActivityType());
            ac.setActivityTypeName(activityType.getName());
            ac.setActivityTypeLevel(activityType.getLevel());
            float total = 0;
            for (Reservation res: reservations) {
                total += res.getNumPeople();
            }

            total = (total/ac.getMaxNumberPeople())*100;
            total = (float) Math.floor(total);
            int ocupation = (int) total;
            ac.setOcupation(ocupation);

            List<ActivityPhotos> imatges = activityPhotosDao.getPhotos(ac.getId());
            if (imatges.size() == 1) {
                ac.setPhotoPrincipal(imatges.get(0).getPhoto());
            } else {
                ac.setImages(imatges);
            }
            ac.setTotalImages(imatges.size());


            activitiesWithOcupation.add(ac);
        }

        Boolean bookingDone = (Boolean) session.getAttribute("bookingDone");
        if (bookingDone != null) {
            model.addAttribute("bookingDone", true);
            model.addAttribute("bookingPagada", bookingDone);
            session.setAttribute("bookingDone", null);
        }

        Boolean passwdUpdated = (Boolean) session.getAttribute("passwdUpdated");
        if (passwdUpdated != null) {
            model.addAttribute("modalAppears", true);
            model.addAttribute("modalInfo", "Contrasenya modificada amb éxit");
            model.addAttribute("modalHref", "/activity/offer");
            session.setAttribute("passwdUpdated", null);
        }

        Boolean profileUpdated = (Boolean) session.getAttribute("profileUpdated");
        if (profileUpdated != null) {
            model.addAttribute("modalAppears", true);
            model.addAttribute("modalInfo", "El perfil s'ha modificat amb éxit");
            model.addAttribute("modalHref", "/activity/offer");
            session.setAttribute("profileUpdated", null);
        }

        Boolean requestRegistered = (Boolean) session.getAttribute("requestRegistered");
        if (requestRegistered != null) {
            model.addAttribute("modalAppears", true);
            model.addAttribute("modalInfo", "Sol·licitud registrada amb éxit");
            model.addAttribute("modalHref", "/activity/offer");
            session.setAttribute("requestRegistered", null);
        }

        Boolean customerReactivated = (Boolean) session.getAttribute("customerReactivated");
        if (customerReactivated != null) {
            model.addAttribute("modalAppears", true);
            model.addAttribute("modalInfo", "Compte resactivat amb éxit");
            model.addAttribute("modalHref", "/activity/offer");
            session.setAttribute("customerReactivated", null);
        }

        model.addAttribute("activities", activitiesWithOcupation);
        model.addAttribute("filtroYorden", new FiltradoYOrden());
        model.addAttribute("comparator", new NameComparator());
        return "activity/offer";
    }

    //Filtrar activitats
    @RequestMapping(value="/offer", method=RequestMethod.POST)
    public String filtro(Model model, @ModelAttribute("filtroYorden") FiltradoYOrden filtro, BindingResult bindingResult) {
        List<Activity> activities = new ArrayList<Activity>();
        switch (filtro.getFiltroCriteri()) {
            case "all":
                activities = activityDao.getActivities("Oberta");
                break;
            case "name":
                activities = activityDao.getActivitiesByName(filtro.getFiltroPatro());
                break;
            case "place":
                activities = activityDao.getActivitiesByPlace(filtro.getFiltroPatro());
                break;
            case "type":
                List<ActivityType> activityTypes = activityTypeDao.getActivityTypesByName(filtro.getFiltroPatro());
                for (ActivityType activityType: activityTypes) {
                    List<Activity> listActivities = activityDao.getActivitiesByActivityType(activityType.getId());
                    for (Activity activity: listActivities) {
                        activities.add(activity);
                    }
                }
                break;
            case "level":
                activityTypes = activityTypeDao.getActivityTypesByLevel(filtro.getFiltroPatro());
                for (ActivityType activityType: activityTypes) {
                    List<Activity> listActivities = activityDao.getActivitiesByActivityType(activityType.getId());
                    for (Activity activity: listActivities) {
                        activities.add(activity);
                    }
                }
                break;
        }

        List<Activity> activitiesWithOcupation = new ArrayList<Activity>();
        for (Activity ac: activities) {
            List<Reservation> reservations = reservationDao.getReservesFromActivity(ac.getId());
            ActivityType activityType = activityTypeDao.getActivityType(ac.getActivityType());
            ac.setActivityTypeName(activityType.getName());
            ac.setActivityTypeLevel(activityType.getLevel());
            float total = 0;
            for (Reservation res: reservations) {
                total += res.getNumPeople();
            }

            total = (total/ac.getMaxNumberPeople())*100;
            total = (float) Math.floor(total);
            int ocupation = (int) total;
            ac.setOcupation(ocupation);

            List<ActivityPhotos> imatges = activityPhotosDao.getPhotos(ac.getId());
            if (imatges.size() == 1) {
                ac.setPhotoPrincipal(imatges.get(0).getPhoto());
            } else {
                ac.setImages(imatges);
            }
            ac.setTotalImages(imatges.size());

            activitiesWithOcupation.add(ac);
        }

        switch (filtro.getOrdenCriteri()) {
            case "name":
                model.addAttribute("comparator", new NameComparator());
                break;
            case "place":
                model.addAttribute("comparator", new PlaceComparator());
                break;
            case "dates":
                model.addAttribute("comparator", new DateComparator());
                break;
            case "type":
                model.addAttribute("comparator", new TypeComparator());
                break;
            case "level":
                model.addAttribute("comparator", new LevelComparator());
                break;
        }

        model.addAttribute("activities", activitiesWithOcupation);
        model.addAttribute("filtroYorden", new FiltradoYOrden());
        return "activity/offer";
    }

    //Mostrar activitats d'un tipus concret
    @RequestMapping("/filtroActivityType/{id}")
    public String filtroActivityType(Model model, @PathVariable int id) {
        ActivityType activityType = activityTypeDao.getActivityType(id);
        if (activityType == null) {
            return "redirect:/";
        }

        List<Activity> activities = activityDao.getActivitiesByActivityType(id);
        List<Activity> activitiesWithOcupation = new ArrayList<Activity>();
        for (Activity ac: activities) {
            List<Reservation> reservations = reservationDao.getReservesFromActivity(ac.getId());
            ac.setActivityTypeName(activityType.getName());
            ac.setActivityTypeLevel(activityType.getLevel());
            float total = 0;
            for (Reservation res: reservations) {
                total += res.getNumPeople();
            }

            total = (total/ac.getMaxNumberPeople())*100;
            total = (float) Math.floor(total);
            int ocupation = (int) total;
            ac.setOcupation(ocupation);

            List<ActivityPhotos> imatges = activityPhotosDao.getPhotos(ac.getId());
            if (imatges.size() == 1) {
                ac.setPhotoPrincipal(imatges.get(0).getPhoto());
            } else {
                ac.setImages(imatges);
            }
            ac.setTotalImages(imatges.size());


            activitiesWithOcupation.add(ac);
        }
        model.addAttribute("activities", activitiesWithOcupation);
        model.addAttribute("filtroYorden", new FiltradoYOrden());
        model.addAttribute("comparator", new NameComparator());
        return "activity/offer";
    }

    //Reservar una activitat
    @RequestMapping("/book/{id}")
    public String bookActivity(HttpSession session, Model model, @PathVariable int id) {
        Activity activity = activityDao.getActivity(id);
        if (activity == null) {
            return "redirect:/";
        }
        int acceso = controlarAcceso(session, "Customer");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "activity/book/" + id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            if (activity.getState().equals("Oberta")) {
                List<ActivityRates> rates = activityRatesDao.getActivityRates(id);


                List<ActivityPhotos> imatges = activityPhotosDao.getPhotos(id);
                if (imatges.size() == 1) {
                    activity.setPhotoPrincipal(imatges.get(0).getPhoto());
                }

                model.addAttribute("imatges", imatges);
                model.addAttribute("totalFotos", imatges.size());
                model.addAttribute("reservation", new Reservation());
                model.addAttribute("activity", activity);
                model.addAttribute("rates", rates);
                return "activity/book";
            } else {
                return "redirect:/";
            }
        } else {
            return "redirect:/";
        }
    }

    //Modificar dades reserva
    @RequestMapping(value="/book/{id}", method = RequestMethod.POST)
    public String dataBookModification(HttpSession session, Model model, @PathVariable int id, @ModelAttribute("reservation") Reservation reservation, BindingResult bindingResult) {
        int acceso = controlarAcceso(session, "Customer");
        if (acceso == USER_AUTHORIZED) {
            List<ActivityRates> rates = activityRatesDao.getActivityRates(id);
            Activity activity = activityDao.getActivity(id);
            List<ActivityPhotos> imatges = activityPhotosDao.getPhotos(id);
            if (imatges.size() == 1) {
                activity.setPhotoPrincipal(imatges.get(0).getPhoto());
            }

            model.addAttribute("imatges", imatges);
            model.addAttribute("totalFotos", imatges.size());
            model.addAttribute("reservation", reservation);
            model.addAttribute("activity", activity);
            model.addAttribute("rates", rates);
            return "activity/book";
        } else {
            return "redirect:/activity/book/" + id;
        }
    }

    //Métode get per a que no falle
    @RequestMapping(value="/summary/{id}")
    public String summaryBooking(@PathVariable int id) {
        return "redirect:/home";
    }

    //Mostrar resum reserva
    @RequestMapping(value="/summary/{id}", method = RequestMethod.POST)
    public String processSummaryBooking(HttpSession session, Model model, @PathVariable int id, @ModelAttribute("reservation") Reservation reservation, BindingResult bindingResult) {
        int acceso = controlarAcceso(session, "Customer");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "activity/summary/" + id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            ReservationValidator reservationValidator = new ReservationValidator();
            reservationValidator.validate(reservation, bindingResult);

            if (bindingResult.hasErrors()) {
                List<ActivityRates> rates = activityRatesDao.getActivityRates(id);
                Activity activity = activityDao.getActivity(id);
                List<ActivityPhotos> imatges = activityPhotosDao.getPhotos(id);
                if (imatges.size() == 1) {
                    activity.setPhotoPrincipal(imatges.get(0).getPhoto());
                }

                model.addAttribute("imatges", imatges);
                model.addAttribute("totalFotos", imatges.size());
                model.addAttribute("activity", activity);
                model.addAttribute("rates", rates);
                System.out.println(bindingResult.toString());
                return "activity/book";
            }


            List<ActivityRates> rates = activityRatesDao.getActivityRates(id);
            Activity activity = activityDao.getActivity(id);

            HashMap<String, Float> t = new HashMap<String, Float>();
            for (ActivityRates rate: rates) {
                t.put(rate.getRateName(), rate.getPrice());
            }

            List<SummaryPrice> prices = reservationDao.calcularPrecio(reservation, activity, t);

            boolean grupo = prices.get(0).isGrupo();
            float totalPrice = 0;
            for (SummaryPrice price: prices) {
                totalPrice += price.getTotalPrice();
            }
            reservation.setTotalPrice(totalPrice);
            reservation.setIdActivity(id);

            model.addAttribute("reservation", reservation);
            model.addAttribute("activity", activity);
            model.addAttribute("rates", rates);
            model.addAttribute("prices", prices);
            model.addAttribute("grupo", grupo);
            model.addAttribute("totalPrice", totalPrice);

            return "activity/summary";
        } else {
            return "redirect:/";
        }
    }

    //Métode get per a que no falle
    @RequestMapping(value="/bookConfirmation/{id}")
    public String confirmBooking(@PathVariable int id) {
        return "redirect:/home";
    }

    //Confirmar reserva
    @RequestMapping(value="/bookConfirmation/{id}", method = RequestMethod.POST)
    public String processConfirmBooking(HttpSession session, Model model, @PathVariable int id, @ModelAttribute("reservation") Reservation reservation, BindingResult bindingResult) {
        int acceso = controlarAcceso(session, "Customer");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "activity/bookConfirmation/" + id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            Random r = new Random();
            int trans = r.nextInt();
            if (trans < 0) {
                trans *= -1;
            }
            reservation.setTransactionNumber(trans);
            reservation.setIdActivity(id);
            Users user = (Users) session.getAttribute("user");
            reservation.setIdCustomer(user.getId());
            Activity activity = activityDao.getActivity(id);

            boolean pagada = reservationDao.addReservation(reservation, activity);

            session.setAttribute("bookingDone", pagada);
            return "redirect:/";
        } else {
            return "redirect:/";
        }

    }

    //Veure dades activitat
    @RequestMapping(value="/view/{id}", method = RequestMethod.GET)
    public String dataViewActivity(Model model, @PathVariable int id) {
        Activity activity = activityDao.getActivity(id);
        if (activity == null) {
            return "redirect:/";
        }
        if (!activity.getState().equals("Cancel·lada")) {
            List<ActivityRates> rates = activityRatesDao.getActivityRates(id);
            List<ActivityPhotos> imatges = activityPhotosDao.getPhotos(id);
            if (imatges.size() == 1) {
                activity.setPhotoPrincipal(imatges.get(0).getPhoto());
            }

            model.addAttribute("imatges", imatges);
            model.addAttribute("totalFotos", imatges.size());
            model.addAttribute("activityType", activityTypeDao.getActivityType(activity.getActivityType()));
            model.addAttribute("activity", activity);
            model.addAttribute("rates", rates);
            return "activity/view";
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

    private int controlarAccesoYId(HttpSession session, String rol, int id) {
        if (session.getAttribute("user") == null) {
            return NOT_LOGGED;
        }

        Users user = (Users) session.getAttribute("user");
        if (user.getRol().equals(rol) && user.getId() == id) {
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

    private void saveFoto(MultipartFile foto, Activity activity, int idFoto) {
        try {
            // Obtener el fichero y guardarlo
            byte[] bytes = foto.getBytes();

            String extension = FilenameUtils.getExtension(foto.getOriginalFilename());
            String direccion = "images/activities/" + activity.getId()  + "_" + idFoto + "." + extension;

            Path path = Paths.get(uploadDirectory + direccion);

            ActivityPhotos aph = new ActivityPhotos();
            aph.setIdActivity(activity.getId());
            aph.setPhotoNumber(idFoto);
            aph.setPhoto("/" + direccion);
            activityPhotosDao.addActivityPhotos(aph);

            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}