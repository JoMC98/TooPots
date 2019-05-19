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

    //Se ejecuta todos los dias a las 00:00 y actualiza el estado de las Actividades que esten a menos de 10 de realizarse cambiando el estado
    @Scheduled(cron = "0 0/15 * ? * * ")
    public void pagarActividades() {
        System.out.println("S'estan pagant les activitats automáticament --> " + new Date());
        List<Activity> activities = activityDao.getActivitiesToPay();
        for (Activity activity: activities) {
            List<Reservation> reservations = reservationDao.getReserves(activity.getId());

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

    @Scheduled(cron = "0 0/15 * ? * * ")
    public void finalitzarActividades() {
        System.out.println("Es cambia l'estat de les activitats automáticament --> " + new Date());
        List<Activity> activities = activityDao.getActivitiesDone();
        for (Activity activity: activities) {
            activity.setState("Completa");
            activityDao.updateActivityState(activity);
        }
    }
}
