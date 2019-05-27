package es.uji.ei1027.toopots.controller;


import es.uji.ei1027.toopots.daos.*;
import es.uji.ei1027.toopots.exceptions.TooPotsException;
import es.uji.ei1027.toopots.model.*;
import es.uji.ei1027.toopots.validator.UserValidator;
import es.uji.ei1027.toopots.validator.CustomerValidator;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {
    private final static int NOT_LOGGED = 1;
    private final static int USER_AUTHORIZED = 2;
    private final static int USER_NOT_AUTHORIZED = 3;

    private CustomerDao customerDao;
    private SubscriptionDao subscriptionDao;
    private UsersDao userDao;
    private ActivityDao activityDao;
    private ActivityTypeDao activityTypeDao;
    private ActivityPhotosDao activityPhotosDao;
    private ActivityRatesDao activityRatesDao;
    private ReservationDao reservationDao;
    private BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();

    @Autowired
    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao=customerDao;
    }

    @Autowired
    public void setSubscriptionDao(SubscriptionDao subscriptionDao) {
        this.subscriptionDao = subscriptionDao;
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

    @Autowired
    public void setReservationDao(ReservationDao reservationDao) {
        this.reservationDao = reservationDao;
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

        try {
            userDao.addUser(user);
        } catch (DuplicateKeyException e) {
            throw new TooPotsException(
                    "El nom d'usuari ja esta en ús", "Prova amb un altre",
                    "UsernameUsed");
        }
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
    public String processUpdateSubmit(HttpSession session, @ModelAttribute("user") Users user, BindingResult bindingResultUser, @ModelAttribute("customer") Customer customer,
                                      BindingResult bindingResultCustomer) {
        int acceso = controlarAcceso(session, "Customer");
        if (acceso == USER_AUTHORIZED) {
            CustomerValidator customerValidator = new CustomerValidator();
            customerValidator.validate(customer, bindingResultCustomer);

            UserValidator userValidator = new UserValidator();
            userValidator.validate(user, bindingResultUser);

            if (bindingResultCustomer.hasErrors() || bindingResultUser.hasErrors()) {
                System.out.println(bindingResultCustomer.toString());
                System.out.println(bindingResultUser.toString());
                return "customer/update";
            }

            Users viejo = (Users) session.getAttribute("user");
            user.setId(viejo.getId());

            try {
                userDao.updateUser(user);
            } catch (DuplicateKeyException e) {
                throw new TooPotsException(
                        "El nom d'usuari ja esta en ús", "Prova amb un altre",
                        "UsernameUsed");
            }
            customer.setId(viejo.getId());
            customerDao.updateCustomer(customer);
            return "redirect:/";
        } else {
            return "redirect:/customer/update";
        }
    }

    //Desactivar compte
    @RequestMapping("/deactivate")
    public String deactivateCustomerAccount(HttpSession session, Model model) {
        int acceso = controlarAcceso(session, "Customer");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/customer/deactivate");
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            Users user = (Users) session.getAttribute("user");
            userDao.updateRole(user.getId(), "Deactivated");
            return "redirect:/logout";
        }
        return "redirect:/";
    }

    //Reactivar compte
    @RequestMapping("/reactivate/{id}")
    public String reactivateCustomerAccount(HttpSession session, Model model, @PathVariable int id) {
        Integer idCanDeactivate = (Integer) session.getAttribute("userCanBeDeactivated");

        if (idCanDeactivate != null && idCanDeactivate == id) {
            Users user = userDao.getUser(id);
            userDao.updateRole(user.getId(), "Customer");
            user.setRol("Customer");
            session.setAttribute("user", user);
            session.setAttribute("userCanBeDeactivated", null);

            String nextUrl = (String) session.getAttribute("nextUrl");
            if (nextUrl == null)
                return "redirect:/";
            else
                return "redirect:" + nextUrl;
        }
        return "redirect:/login";
    }

    //Llistar totes les reserves del client
    @RequestMapping(value= {"/listReservations", "/listReservations/{state}"}, method = RequestMethod.GET)
    public String listReservations(Model model,  HttpSession session, @PathVariable(name="state", required=false) String state) {
        if (state == null || !state.equals("future") && !state.equals("past") && !state.equals("canceled")) {
            state = "all";
        }

        int acceso = controlarAcceso(session, "Customer");

        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "/customer/listReservations");
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            Users user = (Users) session.getAttribute("user");


            List<Reservation> reserves = reservationDao.getReservationsFromCustomer(user.getId());
            //llista que passem al controlador
            List<Activity> activities = new ArrayList<Activity>();

            //crear una llista de activitats mitjançant el id de activitat i anyadir els metodos de reserva
            for (Reservation reserve: reserves){
                Activity activity = activityDao.getActivity(reserve.getIdActivity());

                boolean actividadValida = false;

                if (state.equals("future")) {
                    if (activity.getState().equals("Oberta") || activity.getState().equals("Tancada")) {
                        actividadValida = true;
                    }
                } else if (state.equals("past")) {
                    if (activity.getState().equals("Realitzada")) {
                        actividadValida = true;
                    }
                } else if (state.equals("canceled")) {
                    if (activity.getState().equals("Cancelada")) {
                        actividadValida = true;
                    }
                } else {
                    actividadValida = true;
                }

                if (actividadValida) {
                    activity.setReservationPriceTotal(reserve.getTotalPrice());

                    //obtener el nivel
                    ActivityType activityType = activityTypeDao.getActivityType(activity.getActivityType());
                    activity.setActivityTypeLevel(activityType.getLevel());
                    activity.setActivityTypeName(activityType.getName());

                    List<ActivityPhotos> imatges = activityPhotosDao.getPhotos(activity.getId());
                    if (imatges.size() == 1) {
                        activity.setPhotoPrincipal(imatges.get(0).getPhoto());
                    } else {
                        activity.setImages(imatges);
                    }
                    activity.setTotalImages(imatges.size());
                    activity.setIdReservation(reserve.getId());

                    activities.add(activity);
                }
            }
            model.addAttribute("estat", state);
            model.addAttribute("activities",activities);
            return "customer/listReservations";
        } else {
            return "redirect:/";
        }
    }

    //Llistar tots les subscripcions disponibles
    @RequestMapping(value= {"/listSubscriptions", "/listSubscriptions/{level}"}, method = RequestMethod.GET)
    public String listSubscriptions(Model model, HttpSession session, @PathVariable(name="level", required=false) String level) {
        if (level == null || !level.equals("subscribed") && !level.equals("Baix") && !level.equals("Mitja")
                && !level.equals("Alt") && !level.equals("Extrem")) {
            level = "all";
        }

        int acceso = controlarAcceso(session, "Customer");
        if(acceso == NOT_LOGGED) {
            List<ActivityType> lista;
            if (level.equals("all")) {
                lista = activityTypeDao.getActivityTypes();
            } else if (level.equals("subscribed")) {
                model.addAttribute("user", new Users());
                session.setAttribute("nextUrl", "/customer/listSubscriptions/subscribed");
                return "login";
            } else {
                lista = activityTypeDao.getActivityTypesByLevel(level);
            }

            List<ActivityType> activitiesModified = new ArrayList<ActivityType>();
            for (ActivityType ac: lista) {
                ac.setSubscribe(false);
                activitiesModified.add(ac);
            }

            model.addAttribute("activityTypes", lista);
            model.addAttribute("level", level);
            model.addAttribute("logged", false);
            return "/customer/listSubscriptions";

        } else if (acceso == USER_AUTHORIZED) {
            Users user = (Users) session.getAttribute("user");

            List<ActivityType> lista;
            if (level.equals("all") || level.equals("subscribed")) {
                lista = activityTypeDao.getActivityTypes();
            } else {
                lista = activityTypeDao.getActivityTypesByLevel(level);
            }

            List<Integer> subscriptions = subscriptionDao.getSubscriptions(user.getId());
            List<ActivityType> activitiesModified = new ArrayList<ActivityType>();

            for (ActivityType ac: lista) {
                if(subscriptions.contains(ac.getId())) {
                    ac.setSubscribe(true);
                    activitiesModified.add(ac);
                } else if (!level.equals("subscribed")) {
                    ac.setSubscribe(false);
                    activitiesModified.add(ac);
                }
            }

            model.addAttribute("activityTypes", activitiesModified);
            model.addAttribute("level", level);
            model.addAttribute("logged", true);
            return "customer/listSubscriptions";
        } else {
            return "redirect:/";
        }

    }

    //Subscriure's a una activitat
    @RequestMapping("/subscribe/{id}")
    public String subscribeActivityType(HttpSession session, Model model, @PathVariable int id) {
        ActivityType activityType = activityTypeDao.getActivityType(id);
        if (activityType == null) {
            return "redirect:/";
        }
        int acceso = controlarAcceso(session, "Customer");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "customer/subscribe/"+id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            Users user = (Users) session.getAttribute("user");
            boolean isSuscribed = subscriptionDao.isSuscribed(user.getId(), id);
            if (!isSuscribed) {
                subscriptionDao.addSubscription(id, user.getId());
            }
            return "redirect:/customer/listSubscriptions/subscribed";
        } else {
            return "redirect:/";
        }
    }

    //Desubscriure's a una activitat
    @RequestMapping("/unsubscribe/{id}")
    public String unsubscribeActivityType(HttpSession session, Model model, @PathVariable int id) {
        ActivityType activityType = activityTypeDao.getActivityType(id);
        if (activityType == null) {
            return "redirect:/";
        }
        int acceso = controlarAcceso(session, "Customer");
        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "customer/listSubscriptions");
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            Users user = (Users) session.getAttribute("user");
            subscriptionDao.deleteSubscription(id, user.getId());

            return "redirect:/customer/listSubscriptions/subscribed";
        } else {
            return "redirect:/";
        }
    }

    //Veure dades reserves
    @RequestMapping(value="/viewReservation/{id}", method = RequestMethod.GET)
    public String dataViewReservation(HttpSession session, Model model, @PathVariable int id) {
        Reservation reservation = reservationDao.getReservation(id);
        if (reservation == null) {
            return "redirect:/";
        }
        int acceso = controlarAccesoYId(session, "Customer", reservation.getIdCustomer());

        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "customer/viewReservation/"+id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {

            List<ActivityRates> rates = activityRatesDao.getActivityRates(reservation.getIdActivity());
            Activity activity = activityDao.getActivity(reservation.getIdActivity());

            List<ActivityPhotos> imatges = activityPhotosDao.getPhotos(reservation.getIdActivity());
            if (imatges.size() == 1) {
                activity.setPhotoPrincipal(imatges.get(0).getPhoto());
            }

            boolean pagada = false;
            if (reservation.getState().equals("Pagada")) {
                pagada = true;
            }

            model.addAttribute("imatges", imatges);
            model.addAttribute("pagada", pagada);
            model.addAttribute("totalFotos", imatges.size());
            model.addAttribute("activityType", activityTypeDao.getActivityType(activity.getActivityType()));
            model.addAttribute("activity", activity);
            model.addAttribute("reservation", reservation);
            model.addAttribute("rates", rates);
            return "customer/viewReservation";
        } else {
            return "redirect:/";
        }
    }

    //Cancelar reserva si es posible
    @RequestMapping(value="/cancelReservation/{id}", method = RequestMethod.GET)
    public String cancelReservation(HttpSession session, Model model, @PathVariable int id) {
        Reservation reservation = reservationDao.getReservation(id);
        if (reservation == null) {
            return "redirect:/";
        }
        int acceso = controlarAccesoYId(session, "Customer", reservation.getIdCustomer());

        if(acceso == NOT_LOGGED) {
            model.addAttribute("user", new Users());
            session.setAttribute("nextUrl", "customer/cancelReservation/"+id);
            return "login";
        } else if (acceso == USER_AUTHORIZED) {
            if (reservation.getState().equals("Pendent")) {
                reservationDao.deleteReservation(reservation.getId());
            }
            return "redirect:/customer/listReservations";
        } else {
            return "redirect:/";
        }


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

    private int controlarAccesoYId(HttpSession session, String rol, int id) {
        if (session.getAttribute("user") == null) {
            return NOT_LOGGED;
        }
        Users user = (Users) session.getAttribute("user");
        if (user.getRol().equals(rol) && user.getId() == id) {
            return USER_AUTHORIZED;
        } else {
            return USER_NOT_AUTHORIZED;
        }
    }
}
