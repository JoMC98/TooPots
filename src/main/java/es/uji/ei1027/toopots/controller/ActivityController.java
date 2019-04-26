package es.uji.ei1027.toopots.controller;

import es.uji.ei1027.toopots.daos.ActivityDao;
import es.uji.ei1027.toopots.daos.ActivityPhotosDao;
import es.uji.ei1027.toopots.daos.ActivityRatesDao;
import es.uji.ei1027.toopots.daos.ReservationDao;
import es.uji.ei1027.toopots.model.*;
import es.uji.ei1027.toopots.model.Activity;
import org.apache.commons.io.FilenameUtils;
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
@RequestMapping("/activity")
public class ActivityController {
    private ActivityDao activityDao;
    private ActivityRatesDao activityRatesDao;
    private ActivityPhotosDao activityPhotosDao;
    private ReservationDao reservationDao;

    @Value("${upload.file.directory}")
    private String uploadDirectory;

    @Autowired
    public void setActivityDao(ActivityDao activityDao) {
        this.activityDao=activityDao;
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

    //Llistar totes les activitats
    @RequestMapping("/list")
    public String listActivities(Model model) {
        List<Activity> activities = activityDao.getActivities();
        List<Activity> activitiesWithOcupation = new ArrayList<Activity>();
        for (Activity ac: activities) {
            ActivityPhotos photoPrincipal = activityPhotosDao.getPhotoPrincipal(ac.getId());
            List<Reservation> reservations = reservationDao.getReserves(ac.getId());
            float total = 0;
            for (Reservation res: reservations) {
                total += res.getNumPeople();
            }

            total = (total/ac.getMaxNumberPeople())*100;
            total = (float) Math.floor(total);
            int ocupation = (int) total;

            ac.setOcupation(ocupation);
            ac.setPhotoPrincipal("/" + photoPrincipal.getPhoto());
            activitiesWithOcupation.add(ac);
        }
        model.addAttribute("activities", activitiesWithOcupation);
        return "activity/list";
    }

    //Afegir una nova activitat
    @RequestMapping("/add")
    public String addActivity(Model model) {
        model.addAttribute("activity", new Activity());
        return "activity/add";
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

        activityDao.addActivity(activity);

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
            System.out.println("menor");
            ActivityRates tarifa = activity.getTarifaMenores();
            tarifa.setIdActivity(id);
            activityRatesDao.addActivityRates(tarifa);
        }

        if (activity.getTarifaEstudiantes().getPrice() > 0.0) {
            System.out.println("est");
            ActivityRates tarifa = activity.getTarifaEstudiantes();
            tarifa.setIdActivity(id);
            activityRatesDao.addActivityRates(tarifa);
        }
        if (activity.getTarifaMayores().getPrice() > 0.0) {
            System.out.println("may");
            ActivityRates tarifa = activity.getTarifaMayores();
            tarifa.setIdActivity(id);
            activityRatesDao.addActivityRates(tarifa);
        }
        if (activity.getTarifaGrupos().getPrice() > 0.0) {
            System.out.println("gru");
            ActivityRates tarifa = activity.getTarifaGrupos();
            tarifa.setIdActivity(id);
            activityRatesDao.addActivityRates(tarifa);
        }

        return "redirect:list";
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
            aph.setPhoto(direccion);
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
}
