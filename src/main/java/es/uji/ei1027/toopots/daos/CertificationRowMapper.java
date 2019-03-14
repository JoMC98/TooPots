package es.uji.ei1027.toopots.daos;

import es.uji.ei1027.toopots.model.Certification;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;

public class CertificationRowMapper implements RowMapper<Certification> {
    public Certification mapRow(ResultSet rs, int rowNum) throws SQLException {
        Certification certification = new Certification();
        certification.setId(rs.getInt("idCertification"));
        certification.setCertificate(rs.getString("certificate"));
        certification.setDoc(rs.getString("doc"));
        certification.setState(rs.getString("state"));
        certification.setIdInstructor(rs.getInt("idInstructor"));
        return certification;
    }
}