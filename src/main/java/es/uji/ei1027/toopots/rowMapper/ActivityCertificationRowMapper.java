package es.uji.ei1027.toopots.rowMapper;

import es.uji.ei1027.toopots.model.ActivityCertification;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class ActivityCertificationRowMapper implements RowMapper<ActivityCertification> {
    public ActivityCertification mapRow(ResultSet rs, int rowNum) throws SQLException {
        ActivityCertification activityCertification = new ActivityCertification();
        activityCertification.setIdCertification(rs.getInt("idCertification"));
        activityCertification.setActivityType(rs.getInt("activityType"));
        return activityCertification;
    }
}