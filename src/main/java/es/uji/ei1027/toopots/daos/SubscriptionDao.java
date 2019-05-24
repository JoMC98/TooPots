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

    /* Obté tots les subscripcions del client. Torna una llista buida si no n'hi ha cap. */
    public List<Integer> getSubscriptions(int idCustomer) {
        try {
            return jdbcTemplate.query("SELECT activityType from Subscription WHERE idcustomer=?", new SubscriptionRowMapper(), idCustomer);
        }
        catch(EmptyResultDataAccessException e) {
            return new ArrayList<Integer>();
        }
    }

    /* Informa si el client está subscrit a eixa activitat */
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

    /* Esborra la subscripció */
    public void deleteSubscription(int id, int idCustomer) {
        jdbcTemplate.update("DELETE from Subscription WHERE idcustomer=? AND activityType=?",idCustomer, id);
    }

    /* Afegeix una nova subscripció */
    public void addSubscription(int id, int idCustomer) {
        jdbcTemplate.update("INSERT INTO Subscription VALUES(?, ?)",idCustomer, id);
    }

}
