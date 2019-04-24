package es.uji.ei1027.toopots.controller;

import es.uji.ei1027.toopots.daos.ActivityTypeDao;
import es.uji.ei1027.toopots.model.Activity;
import es.uji.ei1027.toopots.model.ActivityType;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/activityType")
public class ActivityTypeController {
    private ActivityTypeDao activityTypeDao;

    @Value("${upload.file.directory}")
    private String uploadDirectory;

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
    public String processAddSubmit(@RequestParam("foto") MultipartFile foto, @ModelAttribute("activityType") ActivityType activityType, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "activityType/add";

        if (foto.isEmpty()) {
            // Enviar mensaje de error porque no hay fichero seleccionado
            return "instructor/add";
        }

        try {
            // Obtener el fichero y guardarlo
            byte[] bytes = foto.getBytes();

            String extension = FilenameUtils.getExtension(foto.getOriginalFilename());
            String direccion = "images/activityTypes/" + activityType.getName() + "_" + activityType.getLevel() + "." + extension;

            Path path = Paths.get(uploadDirectory + direccion);
            activityType.setPhoto("/" + direccion);

            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
    public String processUpdateSubmit(@RequestParam("foto") MultipartFile foto, @ModelAttribute("activityType") ActivityType activityType, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "activityType/update";

        try {
            // Obtener el fichero y guardarlo
            byte[] bytes = foto.getBytes();

            String extension = FilenameUtils.getExtension(foto.getOriginalFilename());
            String direccion = "images/activityTypes/" + activityType.getName() + "_" + activityType.getLevel() + "." + extension;

            Path path = Paths.get(uploadDirectory + direccion);
            activityType.setPhoto("/" + direccion);

            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        activityTypeDao.updateActivityType(activityType);
        return "redirect:../list";
    }

    //Esborra un tipus de activitat
    @RequestMapping(value="/delete/{id}")
    public String processDelete(@PathVariable int id) {
        Path path = Paths.get(uploadDirectory + activityTypeDao.getActivityType(id).getPhoto());
        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        activityTypeDao.deleteActivityType(id);
        return "redirect:../list";
    }
}
