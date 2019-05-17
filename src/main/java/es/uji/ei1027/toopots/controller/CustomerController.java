package es.uji.ei1027.toopots.controller;


import es.uji.ei1027.toopots.daos.*;
import es.uji.ei1027.toopots.model.*;
import es.uji.ei1027.toopots.validator.UserValidator;
import es.uji.ei1027.toopots.validator.CustomerValidator;
import org.jasypt.util.password.BasicPasswordEncryptor;
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
@RequestMapping("/customer")
public class CustomerController {
    private final static int NOT_LOGGED = 1;
    private final static int USER_AUTHORIZED = 2;
    private final static int USER_NOT_AUTHORIZED = 3;

    private CustomerDao customerDao;
    private UsersDao userDao;
    private ActivityDao activityDao;
    private ActivityTypeDao activityTypeDao;
    private ActivityPhotosDao activityPhotosDao;
    private ActivityRatesDao activityRatesDao;
    private BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();

    @Autowired
    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao=customerDao;
    }

    @Autowired
    public void setUserDao(UsersDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setActivityPhotosDao(ActivityPhotosDao activityPhotosDao) {
        this.activityPhotosDao=activityPhotosDao;
    }

    @Autowired
    public void setActivityRatesDao(ActivityRatesDao activityRatesDao) {
        this.activityRatesDao=activityRatesDao;
    }

    @Autowired
    public void setActivityDao(ActivityDao activityDao) {
        this.activityDao=activityDao;
    }

    @Autowired
    public void setActivityTypeDao(ActivityTypeDao activityTypeDao) {
        this.activityTypeDao=activityTypeDao;
    }

    //Llistar tots els clients
    @RequestMapping("/list")
    public String listCustomers(Model model) {
        model.addAttribute("users", userDao.getCustomers());
        model.addAttribute("customers", customerDao.getCustomers());
        return "customer/list";
    }

    //Afegir un nou client
    @RequestMapping("/add")
    public String addCustomer(Model model) {
        model.addAttribute("user", new Users());
        model.addAttribute("customer", new Customer());
        return "customer/add";
    }

    //Processa la informació del add
    @RequestMapping(value="/add", method=RequestMethod.POST)
    public String processAddSubmit(HttpSession session, @ModelAttribute("user") Users user, BindingResult bindingResultUser, @ModelAttribute("customer") Customer customer,
                                   BindingResult bindingResultCustomer) {

        CustomerValidator customerValidator = new CustomerValidator();
        customerValidator.validate(customer,bindingResultCustomer);

        UserValidator userValidator = new UserValidator();
        userValidator.validate(user,bindingResultUser);

        if (bindingResultCustomer.hasErrors() || bindingResultUser.hasErrors()) {
            System.out.println(bindingResultCustomer.toString());
            System.out.println(bindingResultUser.toString());

            return "customer/add";
        }
        user.setRol("Customer");
        user.setPasswd(passwordEncryptor.encryptPassword(user.getPasswd()));

        userDao.addUser(user);
        Users newUser = userDao.getUser(user.getUsername());

        session.setAttribute("user", newUser);

        customerDao.addCustomer(customer, newUser.getId());
        return "redirect:../home";
    }

    //Actualitzar un client
    @RequestMapping(value="/update", method = RequestMethod.GET)
    public String editCustomer(Model model, HttpSession session) {
        int acceso = controlarAcceso(session, "Customer");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/customer/update");
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            Users user = (Users) session.getAttribute("user");
            model.addAttribute("user", userDao.getUser(user.getId()));
            model.addAttribute("customer", customerDao.getCustomer(user.getId()));
            return "customer/update";
        } else {
            return "redirect:/";
        }

    }

    //Processa la informació del update
    @RequestMapping(value="/update", method = RequestMethod.POST)
    public String processUpdateSubmit(@ModelAttribute("user") Users user, BindingResult bindingResultUser, @ModelAttribute("customer") Customer customer,
                                      BindingResult bindingResultCustomer) {
        CustomerValidator customerValidator = new CustomerValidator();
        customerValidator.validate(customer,bindingResultCustomer);

        UserValidator userValidator = new UserValidator();
        userValidator.validate(user,bindingResultUser);

        if (bindingResultCustomer.hasErrors() || bindingResultUser.hasErrors()) {
            System.out.println(bindingResultCustomer.toString());
            System.out.println(bindingResultUser.toString());
            return "customer/update";
        }

        userDao.updateUser(user);
        customerDao.updateCustomer(customer);
        return "redirect:/";
    }

    //Esborra un client
    @RequestMapping(value="/delete/{id}")
    public String processDelete(@PathVariable int id) {
        userDao.deleteUser(id);
        customerDao.deleteCustomer(id);
        return "redirect:../list";
    }

    //Llistar tots els clients
    @RequestMapping(value="/listReservations", method = RequestMethod.GET)
    public String listReservations(Model model,  HttpSession session) {

        int acceso = controlarAcceso(session, "Customer");

        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/customer/listReservations");
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            Users user = (Users) session.getAttribute("user");
            List<Reservation> reserves = customerDao.getReservations(user.getId());

            //llista que passem al controlador
            List<Activity> activities = new ArrayList<Activity>();

            //crear una llista de activitats mitjançant el id de activitat i anyadir els metodos de reserva
            for (Reservation reserve: reserves){
                Activity activity = activityDao.getActivity(reserve.getIdActivity());
                activity.setReservationPriceTotal(reserve.getTotalPrice());

                //obtener el nivel
                ActivityType activityType = activityTypeDao.getActivityType(activity.getActivityType());
                activity.setActivityTypeLevel(activityType.getLevel());
                activity.setActivityTypeName(activityType.getName());

                //obtener la foto
                ActivityPhotos photoPrincipal = activityPhotosDao.getPhotoPrincipal(activity.getId());
                activity.setPhotoPrincipal(photoPrincipal.getPhoto());
                activities.add(activity);
            }
            model.addAttribute("activities",activities);
            return "customer/listReservations";
        } else {
            return "redirect:/";
        }
    }

    //Llistar tots les subscripcions disponibles
    @RequestMapping(value="/listSubscriptions", method = RequestMethod.GET) // ficar el GET nose si es necessari
    public String listSubscriptions(Model model,  HttpSession session) {

        int acceso = controlarAcceso(session, "Customer");

        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/customer/listSubscriptions");
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            model.addAttribute("activityTypes", activityTypeDao.getActivityTypes());
            return "customer/listReservations";
        } else {
            return "redirect:/";
        }
        //obtindre les activitats a les que es pot subscriure, no se si son totes o no,
        //i si se fa un metode apart per al gestionar el subcriures
        //també mirar lo del boto subscriure, nose si un state o una variable normal
    }

    //Veure dades reserves
    @RequestMapping(value="/viewReservation/{id}", method = RequestMethod.GET)
    public String dataViewReservation(Model model, @PathVariable int id, @ModelAttribute("reservation") Reservation reservation, BindingResult bindingResult) {
        List<ActivityRates> rates = activityRatesDao.getActivityRates(id);
        Activity activity = activityDao.getActivity(id);
        ActivityPhotos photoPrincipal = activityPhotosDao.getPhotoPrincipal(id);
        activity.setPhotoPrincipal(photoPrincipal.getPhoto());

        model.addAttribute("reservation", reservation);
        model.addAttribute("activity", activity);
        model.addAttribute("rates", rates);
        return "customer/viewReservation";
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
