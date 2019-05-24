package es.uji.ei1027.toopots.daos;

import es.uji.ei1027.toopots.model.ActivityRates;
import es.uji.ei1027.toopots.rowMapper.ActivityRatesRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ActivityRatesDao {

    private JdbcTemplate jdbcTemplate;

    // Obté el jdbcTemplate a partir del Data Source
    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /* Afegeix la tarifa a la base de dades */
    public void addActivityRates(ActivityRates activityRates) {
        jdbcTemplate.update("INSERT INTO Activity_rates VALUES(?, ?, ?)",
                activityRates.getIdActivity(), activityRates.getRateName(), activityRates.getPrice());
    }

    /* Esborra la tarifa de la base de dades */
    public void deleteActivityRates(ActivityRates activityRates) {
        jdbcTemplate.update("DELETE from Activity_rates where idActivity=? AND rateName=?", activityRates.getIdActivity(),
                activityRates.getRateName());
    }


    /* Actualitza els atributs de la tarifa
       (excepte el id, que és la clau primària) */
    public void updateActivityRates(ActivityRates activityRates) {
        jdbcTemplate.update("UPDATE Activity_rates SET price=? where idActivity=? AND rateName=?",
                activityRates.getPrice(), activityRates.getIdActivity(), activityRates.getRateName());
    }


    /* Obté les tarifes de la activitat. Torna null si no existeix. */
    public List<ActivityRates> getActivityRates(int id) {
        try {
            return jdbcTemplate.query("SELECT * from Activity_rates WHERE idActivity=?",
                    new ActivityRatesRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
