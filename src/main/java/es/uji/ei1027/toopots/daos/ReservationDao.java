package es.uji.ei1027.toopots.daos;

import es.uji.ei1027.toopots.model.Activity;
import es.uji.ei1027.toopots.model.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReservationDao {
    private ActivityDao activityDao;

    @Autowired
    public void setActivityDao(ActivityDao activityDao) {
        this.activityDao=activityDao;
    }

    private JdbcTemplate jdbcTemplate;

    // Obté el jdbcTemplate a partir del Data Source
    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /* Afegeix la reserva a la base de dades */
    public void addReservation(Reservation reservation) {
        float totalPrice = calcularPrecio(reservation);
        jdbcTemplate.update("INSERT INTO Reservation VALUES(DEFAULT, DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                reservation.getActivityDate(), reservation.getNumUnder16(), reservation.getNumStudents(),
                reservation.getNumAdults(), reservation.getNumOver60(), totalPrice, reservation.getTransactionNumber(),
                reservation.getIdActivity(), reservation.getIdCustomer(), "Pendent");
    }

    private float calcularPrecio(Reservation r) {
        float totalAdults;
        float totalUnder16;
        float totalOver60;
        float totalStudents;

        Activity a = activityDao.getActivity(r.getIdActivity());

        float priceBase = a.getPricePerPerson();

        boolean grupo = false;

//        //ADULTS
//        if (r.getNumPeople() > 9 && t.containsKey("Grups de 10 persones o mes")) {
//            priceBase = t.get("Grups de 10 persones o mes");
//            grupo = true;
//        }
//
//        totalAdults = priceBase * r.getNumAdults();
//
//        //MENORS 16
//        if (t.containsKey("Menors de 16 anys")) {
//            totalUnder16 = t.get("Menors de 16 anys") * r.getNumUnder16();
//        } else {
//            totalUnder16 = priceBase * r.getNumUnder16();
//        }
//
//        //MAJORS 60
//        if (t.containsKey("Majors de 60 anys")){
//            totalOver60 = t.get("Majors de 60 anys") * r.getNumOver60();
//        } else {
//            totalOver60 = priceBase * r.getNumOver60();
//        }
//
//        //ESTUDIANTS
//        if (t.containsKey("Estudiants")){
//            totalStudents = t.get("Estudiants") * r.getNumStudents();
//        } else {
//            totalStudents = priceBase * r.getNumStudents();
//        }

//        return totalUnder16 + totalStudents + totalAdults + totalOver60;
        return 0f;
    }

    /* Esborra la reserva de la base de dades */
    public void deleteReservation(int id) {
        jdbcTemplate.update("DELETE from Reservation where idReservation=?", id);
    }


    /* Actualitza els atributs de la reserva
       (excepte el id, que és la clau primària) */
    public void updateReservation(Reservation reservation) {
        jdbcTemplate.update("UPDATE Reservation SET activityDate=?, numUnder16=?, numStudents=?, numAdults=?, numOver60=?, " +
                        "totalPrice=?, transactionNumber=?, idActivity=?, idCustomer=?, state=? where idReservation=?",
                reservation.getActivityDate(), reservation.getNumUnder16(), reservation.getNumStudents(),
                reservation.getNumAdults(), reservation.getNumOver60(), reservation.getTotalPrice(), reservation.getTransactionNumber(),
                reservation.getIdActivity(), reservation.getIdCustomer(), reservation.getState(), reservation.getId());
    }

    /* Obté la reserva amb el id donat. Torna null si no existeix. */
    public Reservation getReservation(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * from Reservation WHERE idReservation=?",
                    new ReservationRowMapper(), id);
        }
        catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    /* Obté totes les reserves. Torna una llista buida si no n'hi ha cap. */
    public List<Reservation> getReserves() {
        try {
            return jdbcTemplate.query("SELECT * from Reservation", new ReservationRowMapper());
        }
        catch(EmptyResultDataAccessException e) {
            return new ArrayList<Reservation>();
        }
    }

    /* Obté totes les reserves d'una activitat. Torna una llista buida si no n'hi ha cap. */
    public List<Reservation> getReserves(int idActivity) {
        try {
            return jdbcTemplate.query("SELECT * from Reservation where idActivity=?", new ReservationRowMapper(), idActivity);
        }
        catch(EmptyResultDataAccessException e) {
            return new ArrayList<Reservation>();
        }
    }

}
