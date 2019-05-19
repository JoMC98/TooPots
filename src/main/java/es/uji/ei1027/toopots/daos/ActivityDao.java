package es.uji.ei1027.toopots.daos;

import es.uji.ei1027.toopots.model.Activity;
import es.uji.ei1027.toopots.rowMapper.ActivityRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
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
                        "meetingPoint=?, meetingTime=?, activityType=? where idActivity=?",
                activity.getName(), activity.getPlace(), activity.getDescription(), activity.getDates(), activity.getPricePerPerson(),
                activity.getMaxNumberPeople(), activity.getMinNumberPeople(), activity.getMeetingPoint(), activity.getMeetingTime(),
                activity.getActivityType(), activity.getId());
    }

    /* Actualitza el estat de l'activitat*/
    public void updateActivityState(Activity activity) {
        jdbcTemplate.update("UPDATE Activity SET state=? where idActivity=?",
                activity.getState(), activity.getId());
    }

    /* Motiu cancelació l'activitat*/
    public void cancelActivity(Activity activity) {
        jdbcTemplate.update("UPDATE Activity SET cancelationReason=?, state=? where idActivity=?",
                activity.getCancelationReason(), activity.getState(), activity.getId());
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

    /* Obté totes les activitats amb eixe estat. Torna una llista buida si no n'hi ha cap. */
    public List<Activity> getActivities(String state) {
        try {
            return jdbcTemplate.query("SELECT * from Activity where state=?", new ActivityRowMapper(),state);
        }
        catch(EmptyResultDataAccessException e) {
            return new ArrayList<Activity>();
        }
    }

    /* Obté totes les activitats d'un monitor. Torna una llista buida si no n'hi ha cap. */
    public List<Activity> getActivities(int id, String state) {
        try {
            String estat = "";
            if (state.equals("opened")) {
                estat = "Oberta";
            } else if (state.equals("closed")) {
                estat = "Tancada";
            } else if (state.equals("canceled")) {
                estat = "Cancelada";
            } else {
                return jdbcTemplate.query("SELECT * from Activity where idInstructor = ?", new ActivityRowMapper(), id);
            }
            return jdbcTemplate.query("SELECT * from Activity where idInstructor = ? and state=?", new ActivityRowMapper(), id, estat);
        }

        catch(EmptyResultDataAccessException e) {
            return new ArrayList<Activity>();
        }
    }


    /* Obté la activitat. Torna una llista buida si no n'hi ha cap. */
    public Activity getActivityCustomer(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * from Activity where idActivity = ?", new ActivityRowMapper(), id);
        }
        catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    /* Obté totes les activitats amb ixe tipus d'activitat */
    public List<Activity> getActivitiesByActivityType(int idActivityType) {
        try {
            return jdbcTemplate.query("SELECT * from Activity where activityType=?", new ActivityRowMapper(), idActivityType);
        }
        catch(EmptyResultDataAccessException e) {
            return new ArrayList<Activity>();
        }
    }

    /* Obté totes les activitats amb de ixa ubicacio */
    public List<Activity> getActivitiesByPlace(String place) {
        try {
            return jdbcTemplate.query("SELECT * from Activity where UPPER(place) LIKE UPPER(?)", new ActivityRowMapper(), "%" + place + "%");
        }
        catch(EmptyResultDataAccessException e) {
            return new ArrayList<Activity>();
        }
    }

    /* Obté totes les activitats amb aquesta paraula en el nom */
    public List<Activity> getActivitiesByName(String name) {
        try {
            return jdbcTemplate.query("SELECT * from Activity where UPPER(name) LIKE UPPER(?)", new ActivityRowMapper(), "%" + name + "%");
        }
        catch(EmptyResultDataAccessException e) {
            return new ArrayList<Activity>();
        }
    }

    /* Obté totes les activitats que es realitzaran en menys de 10 dies i s'han de pagar */
    public List<Activity> getActivitiesToPay() {
        try {
            return jdbcTemplate.query("SELECT * from Activity where DATE_PART('day', dates::timestamp - current_date::timestamp) < 10 AND dates > current_date AND state IN ('Oberta', 'Tancada')", new ActivityRowMapper());
        }
        catch(EmptyResultDataAccessException e) {
            return new ArrayList<Activity>();
        }
    }

    /* Obté totes les activitats cuya fecha ja ha passat i no s'ha cambiat el estat */
    public List<Activity> getActivitiesDone() {
        try {
            Date d = new Date();
            return jdbcTemplate.query("SELECT * from Activity where current_date > dates AND state IN ('Oberta', 'Tancada')", new ActivityRowMapper());
        } catch(EmptyResultDataAccessException e) {
            return new ArrayList<Activity>();
        }
    }
    
}
