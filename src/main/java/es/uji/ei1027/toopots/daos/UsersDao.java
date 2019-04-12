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
        if (passwordEncryptor.checkPassword(passwd, user.getPasswd())) {
            // Es deuria esborrar de manera segura el camp password abans de tornar-lo
            return user;
        }
        else {
            return null; // bad login!
        }
    }

    /* Afegeix el Client a la base de dades */
    public void addUser(Users user) {
        jdbcTemplate.update("INSERT INTO Users VALUES(DEFAULT, ?, ?, ?, ?, ?, ?, DEFAULT)",
                user.getUsername(), user.getPasswd(), user.getRol(), user.getNif(), user.getName(), user.getMail());
    }

    /* Esborra el Client de la base de dades */
    public void deleteUser(int id) {
        jdbcTemplate.update("DELETE from Users where idUser=?", id);
    }


    /* Actualitza els atributs del Client
       (excepte el id, que és la clau primària) */
    public void updateUser(Users user) {
        jdbcTemplate.update("UPDATE Users SET username=?, passwd=?, rol=?, nif=?, name=?, mail=? where idUser=?",
                user.getUsername(), user.getPasswd(), user.getRol(), user.getNif(), user.getName(), user.getMail(), user.getId());
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


    /* Obté tots els clients. Torna una llista buida si no n'hi ha cap. */
    public List<Users> getUsers() {
        try {
            return jdbcTemplate.query("SELECT * from Users", new UsersRowMapper());
        }
        catch(EmptyResultDataAccessException e) {
            return new ArrayList<Users>();
        }
    }

}