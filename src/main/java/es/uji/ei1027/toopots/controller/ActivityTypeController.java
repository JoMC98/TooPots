package es.uji.ei1027.toopots.controller;

import es.uji.ei1027.toopots.daos.ActivityDao;
import es.uji.ei1027.toopots.daos.ActivityTypeDao;
import es.uji.ei1027.toopots.exceptions.TooPotsException;
import es.uji.ei1027.toopots.model.Activity;
import es.uji.ei1027.toopots.model.ActivityType;
import es.uji.ei1027.toopots.model.Users;
import es.uji.ei1027.toopots.validator.ActivityTypeValidator;
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
import java.util.List;

@Controller
@RequestMapping("/activityType")
public class ActivityTypeController {
    private final static int NOT_LOGGED = 1;
    private final static int USER_AUTHORIZED = 2;
    private final static int USER_NOT_AUTHORIZED = 3;

    private ActivityTypeDao activityTypeDao;
    private ActivityDao activityDao;

    @Value("${upload.file.directory}")
    private String uploadDirectory;

    @Autowired
    public void setActivityTypeDao(ActivityTypeDao activityTypeDao) {
        this.activityTypeDao=activityTypeDao;
    }

    @Autowired
    public void setActivityDao(ActivityDao activityDao) {
        this.activityDao=activityDao;
    }

    //Llistar tots els tipus de activitat
    @RequestMapping(value= {"/list", "/list/{level}"})
    public String listActivityTypes(HttpSession session, Model model, @PathVariable(name="level", required=false) String level) {
        if (level == null || !level.equals("Baix") && !level.equals("Mitja") && !level.equals("Alt") && !level.equals("Extrem")) {
            level = "Totes";
        }
        int acceso = controlarAccesoAdmin(session, "Admin");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "activityType/list");
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            List<ActivityType> lista;
            if (level.equals("Totes")) {
                lista = activityTypeDao.getActivityTypes();
            } else {
                lista = activityTypeDao.getActivityTypesByLevel(level);
            }

            Boolean activityTypeCreated = (Boolean) session.getAttribute("activityTypeCreated");
            if (activityTypeCreated != null) {
                model.addAttribute("modalAppears", true);
                model.addAttribute("modalInfo", "Tipus d'activitat creat amb éxit");
                model.addAttribute("modalHref", "/activityType/list");
                session.setAttribute("activityTypeCreated", null);
            }

            Boolean activityTypeUpdated = (Boolean) session.getAttribute("activityTypeUpdated");
            if (activityTypeUpdated != null) {
                model.addAttribute("modalAppears", true);
                model.addAttribute("modalInfo", "Tipus d'activitat modificada amb éxit");
                model.addAttribute("modalHref", "/activityType/list");
                session.setAttribute("activityTypeUpdated", null);
            }

            Boolean activityTypeDeleted = (Boolean) session.getAttribute("activityTypeDeleted");
            if (activityTypeDeleted != null) {
                model.addAttribute("modalAppears", true);
                model.addAttribute("modalInfo", "Tipus d'activitat borrat amb éxit");
                model.addAttribute("modalHref", "/activityType/list");
                session.setAttribute("activityTypeDeleted", null);
            }

            model.addAttribute("activityTypes", lista);
            model.addAttribute("level", level);
            return "activityType/list";
        } else {
            return "redirect:/";
        }
    }

    //Afegir un nou tipus de activitat
    @RequestMapping("/add")
    public String addActivityType(HttpSession session, Model model) {
        int acceso = controlarAccesoAdmin(session, "Admin");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "activityType/add");
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            model.addAttribute("activityType", new ActivityType());
            model.addAttribute("errorFoto", false);
            return "activityType/add";
        } else {
            return "redirect:/";
        }
    }

    //Processa la informació del add
    @RequestMapping(value="/add", method=RequestMethod.POST)
    public String processAddSubmit(HttpSession session, Model model, @RequestParam("foto") MultipartFile foto, @ModelAttribute("activityType") ActivityType activityType, BindingResult bindingResult) {
        int acceso = controlarAccesoAdmin(session, "Admin");
        if (acceso == USER_AUTHORIZED) {
            if (foto.isEmpty()) {
                model.addAttribute("errorFoto", true);
            }

            ActivityTypeValidator activityTypeValidator = new ActivityTypeValidator();
            activityTypeValidator.validate(activityType, bindingResult);

            if (bindingResult.hasErrors() || foto.isEmpty())
                return "activityType/add";

            try {
                // Obtener el fichero y guardarlo
                byte[] bytes = foto.getBytes();

                String extension = FilenameUtils.getExtension(foto.getOriginalFilename());
                String direccion = "images/activityTypes/" + activityType.getName() + "_" + activityType.getLevel() + "." + extension;

                Path path = Paths.get(uploadDirectory + direccion);
                activityType.setPhoto("/" + direccion);

                try {
                    activityTypeDao.addActivityType(activityType);
                } catch (DuplicateKeyException e) {
                    throw new TooPotsException(
                            "Ja existeix el tipus d'activitat " + activityType.getName() + " amb nivell " + activityType.getLevel(),
                            "Prova un altre nom o nivell",
                            "ClauPrimariaDuplicada");
                }

                Files.write(path, bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }

            session.setAttribute("activityTypeCreated", true);
            return "redirect:list";
        } else {
            return "redirect:/activityType/add";
        }
    }

    //Actualitzar un tipus de activitat
    @RequestMapping(value="/update/{id}", method = RequestMethod.GET)
    public String editActivityType(HttpSession session, Model model, @PathVariable int id) {
        ActivityType activityType = activityTypeDao.getActivityType(id);
        if (activityType == null) {
            return "redirect:/";
        }
        int acceso = controlarAccesoAdmin(session, "Admin");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/activityType/update/" + id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            model.addAttribute("activityType", activityType);
            return "activityType/update";
        } else {
            return "redirect:/";
        }
    }

    //Processa la informació del update
    @RequestMapping(value="/update/{id}", method = RequestMethod.POST)
    public String processUpdateSubmit(HttpSession session, Model model, @PathVariable int id, @RequestParam("foto") MultipartFile foto, @ModelAttribute("activityType") ActivityType activityType,
                                      BindingResult bindingResult) {

        int acceso = controlarAccesoAdmin(session, "Admin");
        if (acceso == USER_AUTHORIZED) {
            ActivityType activityTypeOld = activityTypeDao.getActivityType(id);
            if (!foto.isEmpty()) {
                try {
                    // Obtener el fichero y guardarlo
                    byte[] bytes = foto.getBytes();

                    String extension = FilenameUtils.getExtension(foto.getOriginalFilename());
                    String direccion = "images/activityTypes/" + activityTypeOld.getName() + "_" + activityTypeOld.getLevel() + "." + extension;

                    Path path = Paths.get(uploadDirectory + direccion);
                    borrarFotoActualizada(uploadDirectory + activityType.getPhoto());

                    activityType.setPhoto("/" + direccion);

                    Files.write(path, bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            activityTypeDao.updateActivityType(activityType);
            session.setAttribute("activityTypeUpdated", true);
            return "redirect:../list";
        } else {
            return "redirect:/activityType/update/" + id;
        }

    }



    //Esborra un tipus de activitat
    @RequestMapping(value="/delete/{id}")
    public String processDelete(HttpSession session, Model model, @PathVariable int id) {
        ActivityType activityType = activityTypeDao.getActivityType(id);
        if (activityType == null) {
            return "redirect:/";
        }
        int acceso = controlarAccesoAdmin(session, "Admin");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/activityType/delete/" + id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            List<Activity> activities = activityDao.getAllActivitiesByActivityType(id);
            if (activities.size() == 0) {
                Path path = Paths.get(uploadDirectory + activityType.getPhoto());
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                session.setAttribute("activityTypeDeleted", true);
                activityTypeDao.deleteActivityType(id);
                return "redirect:../list";
            } else {
                model.addAttribute("noEsPosibleBorrar", true);
                model.addAttribute("activityTypes", activityTypeDao.getActivityTypes());
                return "activityType/list";
            }
        } else {
            return "redirect:/";
        }

    }

    private int controlarAccesoAdmin(HttpSession session, String rol) {
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
