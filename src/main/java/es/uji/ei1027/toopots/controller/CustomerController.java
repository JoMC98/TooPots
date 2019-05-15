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
    private ReservationDao reservationDao;
    private ActivityPhotosDao activityPhotosDao;
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
    @RequestMapping(value="/listReservation", method = RequestMethod.GET)
    public String listReservations(Model model,  HttpSession session) {

        int acceso = controlarAcceso(session, "Customer");

        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/customer/update");
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            Users user = (Users) session.getAttribute("user");
            List<Reservation> reserves = customerDao.getReservations(user.getId());
            //llista que passem al controlador
            List<Activity> activities = new ArrayList<Activity>();
            //crear una llista de activitats mitjançant el id de activitat i anyadir els metodos de reserva
            for (Reservation reserve: reserves){
                Activity activity = activityDao.getActivity(reserve.getIdActivity());
                activity.setTotalPrice(reserve.getTotalPrice());
                //obtener el nivel
                ActivityType activityType = activityTypeDao.getActivityType(activity.getActivityType());
                activity.setActivityTypeLevel(activity.getActivityTypeLevel());
                activity.setActivityTypeName(activity.getActivityTypeName());
                //obtener la foto
                ActivityPhotos photoPrincipal = activityPhotosDao.getPhotoPrincipal(activity.getId());
                activity.setPhotoPrincipal(photoPrincipal.getPhoto());
                activities.add(activity);
            }
            model.addAttribute("activities",activities);
            //model.addAttribute("user", userDao.getUser(user.getId()));
            model.addAttribute("customer", customerDao.getCustomer(user.getId()));
            return "customer/listReservations";
        } else {
            return "redirect:/";
        }
        //obtindre les reserves
        //un for reserva per reserva
        //a la vista creo una llista de activitats
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
