package es.uji.ei1027.toopots.controller;

import es.uji.ei1027.toopots.daos.ActivityDao;
import es.uji.ei1027.toopots.daos.ActivityRatesDao;
import es.uji.ei1027.toopots.daos.ReservationDao;
import es.uji.ei1027.toopots.model.*;
import es.uji.ei1027.toopots.model.Activity;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/activity")
public class ActivityController {
    private ActivityDao activityDao;
    private ActivityRatesDao activityRatesDao;
    private ReservationDao reservationDao;

    @Autowired
    public void setActivityDao(ActivityDao activityDao) {
        this.activityDao=activityDao;
    }

    @Autowired
    public void setActivityRatesDao(ActivityRatesDao activityRatesDao) {
        this.activityRatesDao=activityRatesDao;
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
            List<Reservation> reservations = reservationDao.getReserves(ac.getId());
            int total = 0;
            for (Reservation res: reservations) {
                total += res.getNumPeople();
            }
            ac.setOcupation((total/ac.getMaxNumberPeople())*100);
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
    public String processAddSubmit(HttpSession session, @ModelAttribute("activity") Activity activity, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "activity/add";
        //TODO FOTOS

        Users user = (Users) session.getAttribute("user");
        activity.setIdInstructor(user.getId());

        activityDao.addActivity(activity);

        Activity newActivity = activityDao.getActivity(activity.getDates(), activity.getIdInstructor());
        int id = newActivity.getId();

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
