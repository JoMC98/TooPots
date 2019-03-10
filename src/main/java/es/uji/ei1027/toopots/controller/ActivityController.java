package es.uji.ei1027.toopots.controller;

import es.uji.ei1027.toopots.daos.ActivityDao;
import es.uji.ei1027.toopots.model.Activity;
import es.uji.ei1027.toopots.model.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/activity")
public class ActivityController {
    private ActivityDao activityDao;

    @Autowired
    public void setActivityDao(ActivityDao activityDao) {
        this.activityDao=activityDao;
    }

    //Llistar totes les activitats
    @RequestMapping("/list")
    public String listActivities(Model model) {
        model.addAttribute("activities", activityDao.getActivities());
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
    public String processAddSubmit(@ModelAttribute("activity") Activity activity, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "activity/add";
        activityDao.addActivity(activity);
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
