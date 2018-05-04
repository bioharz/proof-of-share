package services;

import io.ebean.Ebean;
import models.User;

public class EbeanUserRepository {

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
