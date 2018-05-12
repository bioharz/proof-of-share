package dao;

import io.ebean.Ebean;
import models.entities.User;

public class UserDao {

    public User getUserByUsername(String username) {
        return Ebean.find(User.class)
                .where().eq("username", username)
                .findOne();
    }

    public void updateUser(User user) {
        Ebean.save(user);
    }

    public void deleteUser(int id) {
        Ebean.delete(User.class, id);
    }
}
