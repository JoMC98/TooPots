package es.uji.ei1027.toopots.daos;

import es.uji.ei1027.toopots.model.ActivityCertification;
import es.uji.ei1027.toopots.model.ActivityType;
import es.uji.ei1027.toopots.model.Certification;
import es.uji.ei1027.toopots.rowMapper.ActivityCertificationRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ActivityCertificationDao {
    private JdbcTemplate jdbcTemplate;
    private ActivityTypeDao activityTypeDao;
    private CertificationDao certificationDao;

    // Obté el jdbcTemplate a partir del Data Source
    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Autowired
    public void setActivityTypeDao(ActivityTypeDao activityTypeDao){this.activityTypeDao=activityTypeDao;}

    @Autowired
    public void setCertificationDao(CertificationDao certificationDao){this.certificationDao=certificationDao;}

    /* Afegeix la Autoritzacio a la base de dades */
    public void addActivityCertification(ActivityCertification activityCertification) {
        jdbcTemplate.update("INSERT INTO Activity_Certification VALUES(?, ?)",
                activityCertification.getIdCertification(), activityCertification.getActivityType());
    }


    /* Obté les activitats assignades al instructor. Torna null si no té cap */
    public List<ActivityType> getAuthorizations(int idInstructor) {
        try {
            List<Certification> certificados = certificationDao.getCertifications(idInstructor);
            List<ActivityType> activities = new ArrayList<ActivityType>();
            for (Certification c: certificados) {
                List<ActivityCertification> aut = jdbcTemplate.query("SELECT * from Activity_Certification where idCertification=?", new ActivityCertificationRowMapper(), c.getId());
                for (ActivityCertification ac: aut) {
                    activities.add(activityTypeDao.getActivityType(ac.getActivityType()));
                }
            }
            return activities;
        }
        catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

}
