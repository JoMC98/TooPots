package es.uji.ei1027.toopots.rowMapper;

import es.uji.ei1027.toopots.model.Users;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class UsersRowMapper implements RowMapper<Users> {
    public Users mapRow(ResultSet rs, int rowNum) throws SQLException {
        Users users = new Users();
        users.setId(rs.getInt("idUser"));
        users.setUsername(rs.getString("username"));
        users.setPasswd(rs.getString("passwd"));
        users.setRol(rs.getString("rol"));
        users.setNif(rs.getString("nif"));
        users.setName(rs.getString("name"));
        users.setMail(rs.getString("mail"));
        users.setRegisterDate(rs.getObject("registerDate", LocalDate.class));
        return users;
    }
}
