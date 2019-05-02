package es.uji.ei1027.toopots.controller;

import es.uji.ei1027.toopots.comparator.*;
import es.uji.ei1027.toopots.daos.*;
import es.uji.ei1027.toopots.exceptions.TooPotsException;
import es.uji.ei1027.toopots.model.*;
import es.uji.ei1027.toopots.model.Activity;
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
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

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
    private ReservationDao reservationDao;
    private ActivityCertificationDao activityCertificationDao;

    @Value("${upload.file.directory}")
    private String uploadDirectory;

    @Autowired
    public void setActivityDao(ActivityDao activityDao) {
        this.activityDao=activityDao;
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


    //Llistar totes les activitats del monitor
    @RequestMapping("/offer")
    public String listActivities(Model model) {
        List<Activity> activities = activityDao.getActivities();

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
    @RequestMapping("/filtrarYordenar")
    public String filtro(Model model, @ModelAttribute("filtroYorden") FiltradoYOrden filtro, BindingResult bindingResult) {
        List<Activity> activities = new ArrayList<Activity>();
        switch (filtro.getFiltroCriteri()) {
            case "name":
                //TODO filtro per nom
                break;
            case "place":
                activities = activityDao.getActivitiesByPlace(filtro.getFiltroPatro());
                break;
            case "dates":
                //TODO filtro per fecha
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
            model.addAttribute("activity", new Activity());
            model.addAttribute("asignadas", asignadas);
            return "activity/add";
        } else {
            return "redirect:/";
        }
    }

    //Processa la informació del add
    @RequestMapping(value="/add", method=RequestMethod.POST)
    public String processAddSubmit(HttpSession session, @ModelAttribute("activity") Activity activity, BindingResult bindingResult,
                                   @RequestParam("foto1") MultipartFile foto1, @RequestParam("foto2") MultipartFile foto2,
                                   @RequestParam("foto3") MultipartFile foto3, @RequestParam("foto4") MultipartFile foto4) {
        if (bindingResult.hasErrors())
            return "activity/add";

        if (foto1.isEmpty()) {
            // Enviar mensaje de error porque no hay fichero seleccionado
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

        saveFoto(foto1, newActivity, 1);

        if (!foto2.isEmpty()) {
            saveFoto(foto2, newActivity, 2);
        }
        if (!foto3.isEmpty()) {
            saveFoto(foto3, newActivity, 3);
        }
        if (!foto4.isEmpty()) {
            saveFoto(foto4, newActivity, 4);
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

        return "redirect:offer";
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
    public String editActivity(Model model, @PathVariable int id) {
        model.addAttribute("activity", activityDao.getActivity(id));
        return "activity/update";
    }

    //Processa la informació del update
    @RequestMapping(value="/update/{id}", method = RequestMethod.POST)
    public String processUpdateSubmit(@PathVariable int id, @ModelAttribute("activity") Activity activity, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "activity/update";
        activityDao.updateActivity(activity);
        return "redirect:../list";
    }

    //Esborra una activitat
    @RequestMapping(value="/delete/{id}")
    public String processDelete(@PathVariable int id) {
        activityDao.deleteActivity(id);
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
