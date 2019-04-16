package es.uji.ei1027.toopots.daos;

import es.uji.ei1027.toopots.model.ActivityRates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ActivityRatesDao {

    private JdbcTemplate jdbcTemplate;

    // Obté el jdbcTemplate a partir del Data Source
    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /* Afegeix l'Activitat a la base de dades */
    public void addActivityRates(ActivityRates activityRates) {
        jdbcTemplate.update("INSERT INTO Activity_rates VALUES(?, ?, ?)",
                activityRates.getIdActivity(), activityRates.getRateName(), activityRates.getPrice());
    }

    /* Esborra l'Activitat de la base de dades */
    public void deleteActivityRates(int id, String rateName) {
        jdbcTemplate.update("DELETE from Activity_rates where idActivity=? AND rateName=?", id, rateName);
    }


    /* Actualitza els atributs de l'Activitat
       (excepte el id, que és la clau primària) */
    public void updateActivityRates(ActivityRates activityRates) {
        jdbcTemplate.update("UPDATE Activity_rates SET price=? where idActivity=? AND rateName=?",
                activityRates.getPrice(), activityRates.getIdActivity(), activityRates.getRateName());
    }

    /* Obté l'Activitat amb el id donat. Torna null si no existeix. */
    public ActivityRates getActivityRates(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * from Activity_rates WHERE idActivity=? AND rateName=?",
                    new ActivityRatesRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /* Obté totes les activitats. Torna una llista buida si no n'hi ha cap. */
    public List<ActivityRates> getActivities() {
        try {
            return jdbcTemplate.query("SELECT * from Activity_rates", new ActivityRatesRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<ActivityRates>();
        }
    }
}
