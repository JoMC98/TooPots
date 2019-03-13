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

    private JdbcTemplate jdbcTemplate;

    // Obté el jdbcTemplate a partir del Data Source
    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /* Afegeix la reserva a la base de dades */
    public void addReservation(Reservation reservation) {
        jdbcTemplate.update("INSERT INTO Reservation VALUES(DEFAULT, DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?)",
                reservation.getActivityDate(),reservation.getNumberPeople(),
                reservation.getPricePerPerson(), reservation.getTotalPrice(), reservation.getTransactionNumber(), reservation.getIdActivity(),
                reservation.getIdCustomer(), "Pendent");
    }

    /* Esborra la reserva de la base de dades */
    public void deleteReservation(int id) {
        jdbcTemplate.update("DELETE from Reservation where idReservation=?", id);
    }


    /* Actualitza els atributs de la reserva
       (excepte el id, que és la clau primària) */
    public void updateReservation(Reservation reservation) {
        jdbcTemplate.update("UPDATE Reservation SET bookingDate=?, activityDate=?, numberPeople=?, PricePerPerson=?, totalPrice=?," +
                        "transactionNumber=?, idActivity=?, idCustomer=?, state=? where idReservation=?",
                reservation.getBookingDate(), reservation.getActivityDate(), reservation.getNumberPeople(), reservation.getPricePerPerson(), reservation.getTotalPrice(),
                reservation.getTransactionNumber(), reservation.getIdActivity(), reservation.getIdCustomer(), reservation.getId());
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

}
