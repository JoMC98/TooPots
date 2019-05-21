package es.uji.ei1027.toopots.controller;

import es.uji.ei1027.toopots.comparator.*;
import es.uji.ei1027.toopots.daos.*;
import es.uji.ei1027.toopots.exceptions.TooPotsException;
import es.uji.ei1027.toopots.model.*;
import es.uji.ei1027.toopots.model.Activity;
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

    //Llistar totes les activitats del monitor
    @RequestMapping("/offer")
    public String listActivities(Model model) {
        List<Activity> activities = activityDao.getActivities("Oberta");
        List<Activity> activitiesWithOcupation = new ArrayList<Activity>();
        for (Activity ac: activities) {
            ActivityPhotos photoPrincipal = activityPhotosDao.getPhotoPrincipal(ac.getId());
            List<Reservation> reservations = reservationDao.getReserves(ac.getId());
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

            ac.setPhotoPrincipal(photoPrincipal.getPhoto());
            activitiesWithOcupation.add(ac);
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
                activities = activityDao.getActivities();
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
            ActivityPhotos photoPrincipal = activityPhotosDao.getPhotoPrincipal(ac.getId());
            List<Reservation> reservations = reservationDao.getReserves(ac.getId());
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

            ac.setPhotoPrincipal(photoPrincipal.getPhoto());
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
        if (foto[0].getSize() == 0) {
            model.addAttribute("errorFoto", true);
        }

        ActivityValidator activityValidator = new ActivityValidator();
        activityValidator.validate(activity, bindingResult);

        if (bindingResult.hasErrors() || foto[0].getSize() == 0) {
            Users user = (Users) session.getAttribute("user");
            List<ActivityType> asignadas = activityCertificationDao.getAuthorizations(user.getId());
            Instructor instructor = instructorDao.getInstructor(user.getId());
            instructor.setActivities(asignadas);
            model.addAttribute("instructor", instructor);
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
                    "Introdueix altra data",
                    "ClauPrimariaDuplicada");
        }


        Activity newActivity = activityDao.getActivity(activity.getDates(), activity.getIdInstructor());
        int id = newActivity.getId();

        for (int i=0; i<foto.length; i++) {
            MultipartFile file = foto[i];
            saveFoto(file, newActivity, i+1);
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

        return "redirect:/";
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

    //Actualitzar una activitat
    @RequestMapping(value="/update/{id}", method = RequestMethod.GET)
    public String editActivity(HttpSession session, Model model, @PathVariable int id) {
        int acceso = controlarAcceso(session, "Instructor");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/activity/update/"+id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            Users user = (Users) session.getAttribute("user");
            List<ActivityType> asignadas = activityCertificationDao.getAuthorizations(user.getId());
            List<ActivityRates> tarifas = activityRatesDao.getActivityRates(id);

            Instructor instructor = instructorDao.getInstructor(user.getId());
            instructor.setActivities(asignadas);
            Activity activity = activityDao.getActivity(id);
            List<ActivityPhotos> photos = activityPhotosDao.getPhotos(id);

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
    }

    //Processa la informació del update
    @RequestMapping(value="/update/{id}", method = RequestMethod.POST)
    public String processUpdateSubmit(Model model, HttpSession session, @PathVariable int id, @ModelAttribute("activity") Activity activity,
                                      BindingResult bindingResult) {

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

        activityDao.updateActivity(activity);
        return "redirect:/";
    }

    //Esborra una activitat
    @RequestMapping(value="/delete/{id}")
    public String processDelete(@PathVariable int id) {
        activityDao.deleteActivity(id);
        return "redirect:../list";
    }

    //Reservar una activitat
    @RequestMapping("/book/{id}")
    public String bookActivity(HttpSession session, Model model, @PathVariable int id) {
        int acceso = controlarAcceso(session, "Customer");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "activity/book/" + id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            List<ActivityRates> rates = activityRatesDao.getActivityRates(id);
            Activity activity = activityDao.getActivity(id);
            ActivityPhotos photoPrincipal = activityPhotosDao.getPhotoPrincipal(id);
            activity.setPhotoPrincipal(photoPrincipal.getPhoto());

            model.addAttribute("reservation", new Reservation());
            model.addAttribute("activity", activity);
            model.addAttribute("rates", rates);
            return "activity/book";
        } else {
            return "redirect:/";
        }
    }

    //Modificar dades reserva
    @RequestMapping(value="/book/{id}", method = RequestMethod.POST)
    public String dataBookModification(Model model, @PathVariable int id, @ModelAttribute("reservation") Reservation reservation, BindingResult bindingResult) {
        List<ActivityRates> rates = activityRatesDao.getActivityRates(id);
        Activity activity = activityDao.getActivity(id);
        ActivityPhotos photoPrincipal = activityPhotosDao.getPhotoPrincipal(id);
        activity.setPhotoPrincipal(photoPrincipal.getPhoto());

        model.addAttribute("reservation", reservation);
        model.addAttribute("activity", activity);
        model.addAttribute("rates", rates);
        return "activity/book";
    }

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
                ActivityPhotos photoPrincipal = activityPhotosDao.getPhotoPrincipal(id);
                activity.setPhotoPrincipal(photoPrincipal.getPhoto());

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

            reservationDao.addReservation(reservation, activity);

            return "redirect:../..";
        } else {
            return "redirect:/";
        }

    }

    //Veure dades activitat
    @RequestMapping(value="/view/{id}", method = RequestMethod.GET)
    public String dataViewActivity(Model model, @PathVariable int id) {
        List<ActivityRates> rates = activityRatesDao.getActivityRates(id);
        Activity activity = activityDao.getActivity(id);
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
    }

    //Controlar una cancelacio
    @RequestMapping("/cancel/{id}")
    public String cancelActivity(HttpSession session, Model model, @PathVariable int id) {
        int acceso = controlarAcceso(session, "Instructor");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "activity/cancel/"+id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            model.addAttribute("activity", activityDao.getActivity(id));
            return "activity/cancel";
        } else {
            return "redirect:/";
        }

    }

    //Processa la informació de la cancelació
    @RequestMapping(value="/cancel/{id}", method = RequestMethod.POST)
    public String processCancelSubmit(Model model, @PathVariable int id, @ModelAttribute("activity") Activity activity,
                                      BindingResult bindingResult) {

        ActivityCancelationValidator cancelationValidator = new ActivityCancelationValidator();
        cancelationValidator.validate(activity, bindingResult);
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.toString());
            return "activity/cancel";
        }
        activity.setState("Cancelada");
        activityDao.cancelActivity(activity);
        return "redirect:/home";
    }

    //Tancar una activitat
    @RequestMapping("/close/{id}")
    public String closeActivity(HttpSession session, Model model, @PathVariable int id) {
        int acceso = controlarAcceso(session, "Instructor");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "activity/close/"+id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            Activity activity = activityDao.getActivity(id);
            activity.setState("Tancada");
            activityDao.updateActivityState(activity);
            return "redirect:/home";
        } else {
            return "redirect:/";
        }
    }

    //Obrir una activitat
    @RequestMapping("/open/{id}")
    public String openActivity(HttpSession session, Model model, @PathVariable int id) {
        int acceso = controlarAcceso(session, "Instructor");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "activity/open/"+id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            Activity activity = activityDao.getActivity(id);
            activity.setState("Oberta");
            activityDao.updateActivityState(activity);
            return "redirect:/home";
        } else {
            return "redirect:/";
        }
    }

    //Llistar totes les reserves d'una activitat
    @RequestMapping(value="/listReservations/{id}", method = RequestMethod.GET)
    public String listReservesActivity(Model model, @PathVariable int id) {
        List<Reservation> reserves = reservationDao.getReserves(id);
        for (Reservation reserve: reserves){
            Users user = usersDao.getUser(reserve.getIdCustomer());
            reserve.setNameCustomer(user.getName());
        }

        model.addAttribute("reserves", reserves);
        return "activity/listReservations";
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