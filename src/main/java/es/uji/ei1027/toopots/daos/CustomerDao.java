package es.uji.ei1027.toopots.daos;


import es.uji.ei1027.toopots.model.Activity;
import es.uji.ei1027.toopots.model.Customer;
import es.uji.ei1027.toopots.model.Reservation;
import es.uji.ei1027.toopots.rowMapper.ActivityRowMapper;
import es.uji.ei1027.toopots.rowMapper.CustomerRowMapper;
import es.uji.ei1027.toopots.rowMapper.ReservationRowMapper;
import es.uji.ei1027.toopots.rowMapper.SubscriptionRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomerDao {

    private JdbcTemplate jdbcTemplate;

    // Obté el jdbcTemplate a partir del Data Source
    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /* Afegeix el Client a la base de dades */
    public void addCustomer(Customer customer, int id) {
        jdbcTemplate.update("INSERT INTO Customer VALUES(?, ?, ?)",
                id, customer.getSex(), customer.getBirthDate());
    }

    /* Esborra el Client de la base de dades */
    public void deleteCustomer(int id) {
        jdbcTemplate.update("DELETE from Customer where idCustomer=?", id);
    }


    /* Actualitza els atributs del Client
       (excepte el id, que és la clau primària) */
    public void updateCustomer(Customer customer) {
        jdbcTemplate.update("UPDATE Customer SET sex=?, birthDate=? where idCustomer=?",
                customer.getSex(), customer.getBirthDate(), customer.getId());
    }

    /* Obté el client amb el id donat. Torna null si no existeix. */
    public Customer getCustomer(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * from Customer WHERE idCustomer=?",
                    new CustomerRowMapper(), id);
        }
        catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    /* Obté tots els clients. Torna una llista buida si no n'hi ha cap. */
    public List<Customer> getCustomers() {
        try {
            return jdbcTemplate.query("SELECT * from Customer", new CustomerRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<Customer>();
        }
    }
    /* Obté tots els clients. Torna una llista buida si no n'hi ha cap. */
    public List<Reservation> getReservations(int id) {
        try {
            return jdbcTemplate.query("SELECT * from Reservation WHERE idcustomer=?", new ReservationRowMapper(), id);
        }
        catch(EmptyResultDataAccessException e) {
            return new ArrayList<Reservation>();
        }
    }

    /* Obté tots les subscripcions dels clients. Torna una llista buida si no n'hi ha cap. */

    public List<Integer> getSubscriptions(int id) {
        try {
            return jdbcTemplate.query("SELECT activityType from Subscription WHERE idcustomer=?", new SubscriptionRowMapper(), id);
        }
        catch(EmptyResultDataAccessException e) {
            return new ArrayList<Integer>();
        }
    }

}
