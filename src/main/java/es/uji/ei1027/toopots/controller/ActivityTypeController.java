package es.uji.ei1027.toopots.controller;

import es.uji.ei1027.toopots.daos.ActivityTypeDao;
import es.uji.ei1027.toopots.model.ActivityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/activityType")
public class ActivityTypeController {
    private ActivityTypeDao activityTypeDao;

    @Autowired
    public void setActivityTypeDao(ActivityTypeDao activityTypeDao) {
        this.activityTypeDao=activityTypeDao;
    }

    //Llistar tots els tipus de activitat
    @RequestMapping("/list")
    public String listActivityTypes(Model model) {
        model.addAttribute("activityTypes", activityTypeDao.getActivityTypes());
        return "activityType/list";
    }

    //Afegir un nou tipus de activitat
    @RequestMapping("/add")
    public String addActivityType(Model model) {
        model.addAttribute("activityType", new ActivityType());
        return "activityType/add";
    }

    //Processa la informació del add
    @RequestMapping(value="/add", method=RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("activityType") ActivityType activityType, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "activityType/add";
        activityTypeDao.addActivityType(activityType);
        return "redirect:list";
    }

    //Actualitzar un tipus de activitat
    @RequestMapping(value="/update/{id}", method = RequestMethod.GET)
    public String editActivityType(Model model, @PathVariable int id) {
        model.addAttribute("activityType", activityTypeDao.getActivityType(id));
        return "activityType/update";
    }

    //Processa la informació del update
    @RequestMapping(value="/update/{id}", method = RequestMethod.POST)
    public String processUpdateSubmit(@PathVariable int id, @ModelAttribute("activityType") ActivityType activityType, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "activityType/update";
        activityTypeDao.updateActivityType(activityType);
        return "redirect:../list";
    }

    //Esborra un tipus de activitat
    @RequestMapping(value="/delete/{id}")
    public String processDelete(@PathVariable int id) {
        activityTypeDao.deleteActivityType(id);
        return "redirect:../list";
    }
}
