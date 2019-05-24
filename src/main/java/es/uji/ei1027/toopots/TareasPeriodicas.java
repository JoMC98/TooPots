package es.uji.ei1027.toopots;

import es.uji.ei1027.toopots.daos.ActivityDao;
import es.uji.ei1027.toopots.daos.CustomerDao;
import es.uji.ei1027.toopots.daos.ReservationDao;
import es.uji.ei1027.toopots.model.Activity;
import es.uji.ei1027.toopots.model.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class TareasPeriodicas {

    private CustomerDao customerDao;
    private ActivityDao activityDao;
    private ReservationDao reservationDao;

    @Autowired
    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao=customerDao;
    }

    @Autowired
    public void setActivityDao(ActivityDao activityDao) {
        this.activityDao=activityDao;
    }

    @Autowired
    public void setReservationDao(ReservationDao reservationDao) {
        this.reservationDao = reservationDao;
    }

    //Se executa tots els dies cada 15 min i actualitza l'estat de les Reserves d'Activitats que es realitzaran en menys de 10 dies ("Pagada")
    @Scheduled(cron = "0 0/15 * ? * * ")
    public void pagarActividades() {
        System.out.println("S'estan pagant les activitats automáticament --> " + new Date());
        List<Activity> activities = activityDao.getActivitiesToPay();
        for (Activity activity: activities) {
            List<Reservation> reservations = reservationDao.getReservesFromActivity(activity.getId());

            //Si l'activitat té reserves
            if (reservations.size() > 0) {
                for (Reservation reservation: reservations) {
                    if (reservation.getState().equals("Pendent")) {
                        reservationDao.payReservation(reservation);
                    }
                }
            }
        }
    }

    //Se executa tots els dies cada 15 min i actualitza l'estat de les Activitats que ja s'han realitzat
    @Scheduled(cron = "0 0/15 * ? * * ")
    public void finalitzarActividades() {
        System.out.println("Es cambia l'estat de les activitats automáticament --> " + new Date());
        List<Activity> activities = activityDao.getActivitiesDone();
        for (Activity activity: activities) {
            activity.setState("Realitzada");
            activityDao.updateActivityState(activity);
        }
    }
}
