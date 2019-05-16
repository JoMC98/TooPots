package es.uji.ei1027.toopots.controller;

import es.uji.ei1027.toopots.daos.ActivityDao;
import es.uji.ei1027.toopots.daos.ReservationDao;
import es.uji.ei1027.toopots.daos.UserDao;
import es.uji.ei1027.toopots.daos.UsersDao;
import es.uji.ei1027.toopots.model.Activity;
import es.uji.ei1027.toopots.model.ActivityType;
import es.uji.ei1027.toopots.model.Reservation;
import es.uji.ei1027.toopots.model.Users;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@RequestMapping("/reservation")
public class ReservationController {
    private ReservationDao reservationDao;
    private ActivityDao activityDao;
    private UsersDao usersDao;

    @Autowired
    public void setReservationDao(ReservationDao reservationDao) {
        this.reservationDao=reservationDao;
    }

    @Autowired
    public void setNameCustomer(UserDao userDao) {
        this.usersDao=usersDao;
    }

    //Llistar totes les reserves
    @RequestMapping("/list")
    public String listReserves(Model model) {
        model.addAttribute("reserves", reservationDao.getReserves());
        return "reservation/list";
    }

    //Llistar totes les reserves d'una activitat
    @RequestMapping(value="/listReservations/{id}", method = RequestMethod.GET)
    public String listReservesActivity(Model model, @PathVariable int id) {
        List<Reservation> reserves = reservationDao.getReserves(id);
        String userName = "";
        for (Reservation reserve: reserves){
            Users user = usersDao.getUser(reserve.getIdCustomer());
            reserve.setNameCustomer(user.getUsername());
            //userName = reserve.getNameCustomer(user.getUsername());
        }

        //reservas de la activitat
        model.addAttribute("userName", usersDao.getUser(userName));
        model.addAttribute("reserves", reservationDao.getReserves(id));
        return "activity/listReservations";
    }

    //Afegir una nova reserva
    @RequestMapping("/add")
    public String addReserve(Model model) {
        model.addAttribute("reservation", new Reservation());
        return "reservation/add";
    }

    //Processa la informació del add
    @RequestMapping(value="/add", method=RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("reservation") Reservation reservation, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "reservation/add";
        reservationDao.addReservation(reservation);
        return "redirect:list";
    }

    //Actualitzar una reserva
    @RequestMapping(value="/update/{id}", method = RequestMethod.GET)
    public String editActivity(Model model, @PathVariable int id) {
        model.addAttribute("reservation", reservationDao.getReservation(id));
        return "reservation/update";
    }

    //Processa la informació del update
    @RequestMapping(value="/update/{id}", method = RequestMethod.POST)
    public String processUpdateSubmit(@PathVariable int id, @ModelAttribute("reservation") Reservation reservation, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "reservation/update";
        reservationDao.updateReservation(reservation);
        return "redirect:../list";
    }

    //Esborra una activitat
    @RequestMapping(value="/delete/{id}")
    public String processDelete(@PathVariable int id) {
        reservationDao.deleteReservation(id);
        return "redirect:../list";
    }
}