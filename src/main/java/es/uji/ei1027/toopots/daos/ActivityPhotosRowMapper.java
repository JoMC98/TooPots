package es.uji.ei1027.toopots.daos;

import es.uji.ei1027.toopots.model.ActivityPhotos;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ActivityPhotosRowMapper implements RowMapper<ActivityPhotos> {
    public ActivityPhotos mapRow(ResultSet rs, int rowNum) throws SQLException {
        ActivityPhotos activityPhotos = new ActivityPhotos();
        activityPhotos.setIdActivity(rs.getInt("idActivity"));
        activityPhotos.setPhotoNumber(rs.getInt("photoNumber"));
        activityPhotos.setPhoto(rs.getString("photo"));
        return activityPhotos;
    }
}