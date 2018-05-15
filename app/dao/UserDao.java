package dao;

import io.ebean.Ebean;
import models.entities.User;
import play.Logger;

public class UserDao {

    public User getUserByUsername(String username) {
        try {
            return Ebean.find(User.class)
                    .where().eq("username", username)
                    .findOne();
        } catch (Exception e) {
            Logger.error("Can't fine the user with username: " + username + ". Error: " + e);
            return null;
        }
    }

    public void updateUser(User user) {
        Ebean.save(user);
    }

    public void deleteUser(int id) {
        Ebean.delete(User.class, id);
    }
}
