package es.uji.ei1027.toopots.daos;

import es.uji.ei1027.toopots.model.Users;

import java.util.Collection;

public interface UserDao {
    Users loadUserByUsername(String username, String password);
}
