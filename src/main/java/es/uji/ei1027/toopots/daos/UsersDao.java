package es.uji.ei1027.toopots.daos;

import es.uji.ei1027.toopots.model.Users;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

    /* Esborra el Usuari de la base de dades */
    public void deleteUser(int id) {
        jdbcTemplate.update("DELETE from Users where idUser=?", id);
    }


    /* Actualitza els atributs del Usuari
       (excepte el id, que és la clau primària) */
    public void updateUser(Users user) {
        jdbcTemplate.update("UPDATE Users SET username=?, nif=?, name=?, mail=? where idUser=?",
                user.getUsername(), user.getNif(), user.getName(), user.getMail(), user.getId());
    }

    /* Actualitza la contrasenya */
    public void updatePassword(Users user) {
        jdbcTemplate.update("UPDATE Users SET passwd=? where idUser=?",
                user.getPasswd(), user.getId());
    }

    /* Obté el client amb el id donat. Torna null si no existeix. */
    public Users getUser(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * from Users WHERE idUser=?",
                    new UsersRowMapper(), id);
        }
        catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    /* Obté el client amb el username donat. Torna null si no existeix. */
    public Users getUser(String username) {
        try {
            return jdbcTemplate.queryForObject("SELECT * from Users WHERE username=?", new UsersRowMapper(), username);
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

    /* Obté tots els clients. Torna una llista buida si no n'hi ha cap. */
    public List<Users> getCustomers() {
        try {
            return jdbcTemplate.query("SELECT * from Users where rol='Customer'", new UsersRowMapper());
        }
        catch(EmptyResultDataAccessException e) {
            return new ArrayList<Users>();
        }
    }

    /* Obté totes les sol·licituds  Torna una llista buida si no n'hi ha cap. */
    /*
    public List<Users> getRequest() {
        try {
            return jdbcTemplate.query("SELECT * from Users where rol='Request'", new UsersRowMapper());
        }
        catch(EmptyResultDataAccessException e) {
            return new ArrayList<Users>();
        }
    }*/


}