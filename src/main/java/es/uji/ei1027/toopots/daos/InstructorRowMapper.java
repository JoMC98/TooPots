package es.uji.ei1027.toopots.daos;

import es.uji.ei1027.toopots.model.Instructor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InstructorRowMapper implements RowMapper<Instructor> {

    @Override
    public Instructor mapRow(ResultSet rs, int rowNum) throws SQLException {
        Instructor instructor = new Instructor();

        instructor.setId(rs.getInt("idInstructor"));
        instructor.setPhoto(rs.getString("photo"));
        instructor.setResidence(rs.getString("residence"));
        instructor.setAccountNumber(rs.getInt("accountNumber"));
        return instructor;
    }
}
