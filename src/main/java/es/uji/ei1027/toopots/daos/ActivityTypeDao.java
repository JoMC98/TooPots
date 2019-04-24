package es.uji.ei1027.toopots.daos;

import es.uji.ei1027.toopots.model.ActivityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ActivityTypeDao {
    private JdbcTemplate jdbcTemplate;

    // Obté el jdbcTemplate a partir del Data Source
    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /* Afegeix el Tipus de Activitat a la base de dades */
    public void addActivityType(ActivityType activityType) {
        jdbcTemplate.update("INSERT INTO Activity_Type VALUES(DEFAULT, ?, ?, ?, ?)",
                activityType.getName(), activityType.getLevel(),
                activityType.getDescription(), activityType.getPhoto());
    }

    /* Esborra el activityType de la base de dades */
    public void deleteActivityType(int id) {
        jdbcTemplate.update("DELETE from Activity_Type where idActivityType=?", id);
    }


    /* Actualitza els atributs del activityType
       (excepte el id, que és la clau primària) */
    public void updateActivityType(ActivityType activityType) {
        jdbcTemplate.update("UPDATE Activity_Type SET description=?, photo=? where idActivityType=?",
                activityType.getDescription(), activityType.getPhoto(), activityType.getId());
    }

    /* Obté el activityType amb el id donat. Torna null si no existeix. */
    public ActivityType getActivityType(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * from Activity_Type WHERE idActivityType=?",
                    new ActivityTypeRowMapper(), id);
        }
        catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    /* Obté tots els activityTypes. Torna una llista buida si no n'hi ha cap. */
    public List<ActivityType> getActivityTypes() {
        try {
            return jdbcTemplate.query("SELECT * from Activity_Type", new ActivityTypeRowMapper());
        }
        catch(EmptyResultDataAccessException e) {
            return new ArrayList<ActivityType>();
        }
    }
}
