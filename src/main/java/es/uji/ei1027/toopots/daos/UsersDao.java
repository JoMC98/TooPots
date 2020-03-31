package es.uji.ei1027.toopots.daos;

import es.uji.ei1027.toopots.model.Users;
import es.uji.ei1027.toopots.rowMapper.UsersRowMapper;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UsersDao implements UserDao {

    private JdbcTemplate jdbcTemplate;

    // Obté el jdbcTemplate a partir del Data Source
    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Users loadUserByUsername(String username, String passwd) {
        Users user = getUser(username);
        System.out.println(user);

        if (user == null)
            return null; // Usuari no trobat

        // Contrasenya
        BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();

        boolean ret = passwordEncryptor.checkPassword(passwd, user.getPasswd());

        if (ret) {
            return user;
        }
        else {
            return null; // bad login!
        }
    }

    /* Afegeix el Usuari a la base de dades */
    public void addUser(Users user) {
        jdbcTemplate.update("INSERT INTO Users VALUES(DEFAULT, ?, ?, ?, ?, ?, ?, DEFAULT)",
                user.getUsername(), user.getPasswd(), user.getRol(), user.getNif(), user.getName(), user.getMail());
    }

    /* Actualitza els atributs del Usuari
       (excepte el id, que és la clau primària) */
    public void updateUser(Users user) {
        jdbcTemplate.update("UPDATE Users SET username=?, nif=?, name=?, mail=? where idUser=?",
                user.getUsername(), user.getNif(), user.getName(), user.getMail(), user.getId());
    }

    /* Actualitza el rol de l'usuari */
    public void updateRole(int idUser, String role) {
        jdbcTemplate.update("UPDATE Users SET rol=? where idUser=?", role, idUser);
    }

    /* Actualitza la contrasenya de l'ussuari */
    public void updatePassword(Users user) {
        jdbcTemplate.update("UPDATE Users SET passwd=? where idUser=?",
                user.getPasswd(), user.getId());
    }

    /* Obté l'usuari amb el id donat. Torna null si no existeix. */
    public Users getUser(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * from Users WHERE idUser=?",
                    new UsersRowMapper(), id);
        }
        catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    /* Obté l'usuari amb el username donat. Torna null si no existeix. */
    public Users getUser(String username) {
        try {
            return jdbcTemplate.queryForObject("SELECT * from Users WHERE username=" +  username, new UsersRowMapper());
        }
        catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    /* Obté tots els instructors ja acceptats. Torna una llista buida si no n'hi ha cap. */
    public List<Users> getInstructors() {
        try {
            return jdbcTemplate.query("SELECT * from Users where rol='Instructor'", new UsersRowMapper());
        }
        catch(EmptyResultDataAccessException e) {
            return new ArrayList<Users>();
        }
    }

    /* Obté totes les sol·licituds de monitor. Torna una llista buida si no n'hi ha cap. */
    public List<Users> getRequests() {
        try {
            return jdbcTemplate.query("SELECT * from Users where rol='Request'", new UsersRowMapper());
        }
        catch(EmptyResultDataAccessException e) {
            return new ArrayList<Users>();
        }
    }
}