package es.uji.ei1027.toopots.daos;

import es.uji.ei1027.toopots.model.Activity;
import es.uji.ei1027.toopots.model.Reservation;
import es.uji.ei1027.toopots.model.SummaryPrice;
import es.uji.ei1027.toopots.rowMapper.ReservationRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
    public boolean addReservation(Reservation reservation, Activity activity) {
        Date actualDate = new Date();
        Date date = Date.from(activity.getDates().atStartOfDay(ZoneId.systemDefault()).toInstant());

        int dias = (int) ((date.getTime()-actualDate.getTime())/86400000);
        String state = "Pendent";
        if (dias < 10) {
            System.out.println("Ja va pagaeta");
            state = "Pagada";
        }
        jdbcTemplate.update("INSERT INTO Reservation VALUES(DEFAULT, DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                reservation.getNumUnder16(), reservation.getNumStudents(),
                reservation.getNumAdults(), reservation.getNumOver60(), reservation.getTotalPrice(), reservation.getTransactionNumber(),
                reservation.getIdActivity(), reservation.getIdCustomer(), state);

        return state=="Pagada";
    }

    public List<SummaryPrice> calcularPrecio(Reservation r, Activity a, HashMap<String, Float> t) {
        float totalAdults;
        float totalUnder16;
        float totalOver60;
        float totalStudents;

        float priceBase = a.getPricePerPerson();
        float priceEstudiants;
        float priceMenors;
        float priceMajors;

        boolean grupo = false;

        //ADULTS
        if (r.getNumPeople() > 9 && t.containsKey("Grups de 10 persones o mes")) {
            priceBase = t.get("Grups de 10 persones o mes");
            grupo = true;
        }

        //MENORS 16
        if (t.containsKey("Menors de 16 anys")) {
            priceMenors = t.get("Menors de 16 anys");

        } else {
            priceMenors = priceBase;
        }

        //MAJORS 60
        if (t.containsKey("Majors de 60 anys")){
            priceMajors = t.get("Majors de 60 anys");
        } else {
            priceMajors = priceBase;
        }

        //ESTUDIANTS
        if (t.containsKey("Estudiants")){
            priceEstudiants = t.get("Estudiants");
        } else {
            priceEstudiants = priceBase;
        }

        totalAdults = priceBase * r.getNumAdults();
        totalUnder16 = priceMenors * r.getNumUnder16();
        totalOver60 = priceMajors * r.getNumOver60();
        totalStudents = priceEstudiants * r.getNumStudents();


        List<SummaryPrice> prices = new ArrayList<SummaryPrice>();
        prices.add(new SummaryPrice("Adults", r.getNumAdults(), priceBase, totalAdults, grupo));
        prices.add(new SummaryPrice("Estudiants", r.getNumStudents(), priceEstudiants, totalStudents, grupo));
        prices.add(new SummaryPrice("Menors de 16 anys", r.getNumUnder16(), priceMenors, totalUnder16, grupo));
        prices.add(new SummaryPrice("Majors de 60 anys", r.getNumOver60(), priceMajors, totalOver60, grupo));

        return prices;
    }

    /* Esborra la reserva de la base de dades */
    public void deleteReservation(int id) {
        jdbcTemplate.update("DELETE from Reservation where idReservation=?", id);
    }


    /* Actualitza l'estat a pagat */
    public void payReservation(Reservation reservation) {
        jdbcTemplate.update("UPDATE Reservation SET state=? where idReservation=?",
                "Pagada", reservation.getId());
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


    /* Obté totes les reserves d'una activitat. Torna una llista buida si no n'hi ha cap. */
    public List<Reservation> getReservesFromActivity(int idActivity) {
        try {
            return jdbcTemplate.query("SELECT * from Reservation where idActivity=?", new ReservationRowMapper(), idActivity);
        }
        catch(EmptyResultDataAccessException e) {
            return new ArrayList<Reservation>();
        }
    }

    /* Obté totes les reserves d'un client. Torna una llista buida si no n'hi ha cap. */
    public List<Reservation> getReservationsFromCustomer(int idCustomer) {
        try {
            return jdbcTemplate.query("SELECT * from Reservation WHERE idcustomer=?", new ReservationRowMapper(), idCustomer);
        }
        catch(EmptyResultDataAccessException e) {
            return new ArrayList<Reservation>();
        }
    }

}
