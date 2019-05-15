package es.uji.ei1027.toopots.daos;

import es.uji.ei1027.toopots.model.ActivityPhotos;
import es.uji.ei1027.toopots.rowMapper.ActivityPhotosRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ActivityPhotosDao {

    private JdbcTemplate jdbcTemplate;

    // Obté el jdbcTemplate a partir del Data Source
    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /* Afegeix la foto a la base de dades */
    public void addActivityPhotos(ActivityPhotos activityPhotos) {
        jdbcTemplate.update("INSERT INTO Activity_Photos VALUES(?, ?, ?)",
                activityPhotos.getIdActivity(), activityPhotos.getPhotoNumber(), activityPhotos.getPhoto());
    }

    /* Esborra la foto de la base de dades */
    public void deleteActivityPhotos(int id, int photoNumber) {
        jdbcTemplate.update("DELETE from Activity_Photos where idActivity=? AND photoNumber=?", id, photoNumber);
    }


    /* Obté la foto amb el id donat. Torna null si no existeix. */
    public ActivityPhotos getActivityPhotos(int id, int photoNumber) {
        try {
            return jdbcTemplate.queryForObject("SELECT * from Activity_Photos WHERE idActivity=? AND photoNumber=?",
                    new ActivityPhotosRowMapper(), id, photoNumber);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /* Obté totes les fotos. Torna una llista buida si no n'hi ha cap. */
    public List<ActivityPhotos> getActivities() {
        try {
            return jdbcTemplate.query("SELECT * from Activity_Photos", new ActivityPhotosRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<ActivityPhotos>();
        }
    }

    /* Obté totes les fotos d'una activitat. Torna una llista buida si no n'hi ha cap. */
    public List<ActivityPhotos> getPhotos(int id) {
        try {
            return jdbcTemplate.query("SELECT * from Activity_Photos where idActivity=?", new ActivityPhotosRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<ActivityPhotos>();
        }
    }

    /* Obté la foto principal d'una activitat. Torna una llista buida si no n'hi ha cap. */
    public ActivityPhotos getPhotoPrincipal(int id) {
        try {
            List<ActivityPhotos> lista = getPhotos(id);
            for (ActivityPhotos ph: lista) {
                if (ph.getPhotoNumber() == 1) {
                    return ph;
                }
            }
        } catch (EmptyResultDataAccessException e) {
            return new ActivityPhotos();
        }
        return null;
    }
}
