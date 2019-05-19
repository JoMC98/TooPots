package es.uji.ei1027.toopots.daos;

import es.uji.ei1027.toopots.rowMapper.SubscriptionRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SubscriptionDao {

    private JdbcTemplate jdbcTemplate;

    // Obté el jdbcTemplate a partir del Data Source
    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
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

    public boolean isSuscribed(int idCustomer, int idActivityType) {
        try {
            List<Integer> lista = jdbcTemplate.query("SELECT activityType from Subscription WHERE idcustomer=? AND activityType=?",
                    new SubscriptionRowMapper(), idCustomer, idActivityType);
            if (lista.size() == 0)
                return false;
            return true;
        }
        catch(EmptyResultDataAccessException e) {
            return false;
        }
    }

    public void deleteSubscription(int id) {
        jdbcTemplate.update("DELETE from Subscription WHERE activityType=?", id);
    }

    public void addSubscription(int id, int idCustomer) {
        jdbcTemplate.update("INSERT INTO Subscription VALUES(?, ?)",idCustomer, id);
    }

}
