package es.uji.ei1027.toopots.daos;

import es.uji.ei1027.toopots.model.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ActivityDao {
    private JdbcTemplate jdbcTemplate;

    // Obté el jdbcTemplate a partir del Data Source
    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /* Afegeix l'Activitat a la base de dades */
    public void addActivity(Activity activity) {
        jdbcTemplate.update("INSERT INTO Activity VALUES(DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, DEFAULT, ?, ?, ?)", activity.getName(),
                activity.getPlace(), activity.getDescription(),activity.getDates(), activity.getPricePerPerson(), activity.getMaxNumberPeople(),
                activity.getMinNumberPeople(),activity.getMeetingPoint(), activity.getMeetingTime(), "Oberta", activity.getActivityType(),
                activity.getIdInstructor());
    }

    /* Esborra l'Activitat de la base de dades */
    public void deleteActivity(int id) {
        jdbcTemplate.update("DELETE from Activity where idActivity=?", id);
    }


    /* Actualitza els atributs de l'Activitat
       (excepte el id, que és la clau primària) */
    public void updateActivity(Activity activity) {
        jdbcTemplate.update("UPDATE Activity SET name=?, place=?, description=?, dates=?, pricePerPerson=?, maxNumberPeople=?, minNumberPeople=?," +
                        "meetingPoint=?, meetingTime=?, state=?, activityType=?, idInstructor=? where idActivity=?",
                activity.getName(), activity.getPlace(), activity.getDescription(), activity.getDates(), activity.getPricePerPerson(),
                activity.getMaxNumberPeople(), activity.getMinNumberPeople(), activity.getMeetingPoint(), activity.getMeetingTime(),
                activity.getState(), activity.getActivityType(), activity.getIdInstructor(), activity.getId());
    }

    /* Obté l'Activitat amb el id donat. Torna null si no existeix. */
    public Activity getActivity(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * from Activity WHERE idActivity=?",
                    new ActivityRowMapper(), id);
        }
        catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Activity getActivity(LocalDate dates, int idInstructor) {
        try {
            return jdbcTemplate.queryForObject("SELECT * from Activity WHERE dates=? AND idInstructor=?",
                    new ActivityRowMapper(), dates, idInstructor);
        }
        catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    /* Obté totes les activitats. Torna una llista buida si no n'hi ha cap. */
    public List<Activity> getActivities() {
        try {
            return jdbcTemplate.query("SELECT * from Activity", new ActivityRowMapper());
        }
        catch(EmptyResultDataAccessException e) {
            return new ArrayList<Activity>();
        }
    }

    
}
