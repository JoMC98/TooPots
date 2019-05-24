package es.uji.ei1027.toopots.daos;


import es.uji.ei1027.toopots.model.Customer;
import es.uji.ei1027.toopots.model.Reservation;
import es.uji.ei1027.toopots.rowMapper.CustomerRowMapper;
import es.uji.ei1027.toopots.rowMapper.ReservationRowMapper;
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




}
