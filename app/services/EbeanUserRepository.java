package services;

import io.ebean.Ebean;
import models.User;

public class EbeanUserRepository {

    public EbeanUserRepository() {

        if (Ebean.find(User.class).findCount() <= 0) {
            User user1 = new User();
            user1.setUsername("user1");
            user1.setPasswordInClear("password1");
            Ebean.save(user1);

            User user2 = new User();
            user2.setUsername("user2");
            user2.setPasswordInClear("password2");
            Ebean.save(user2);

            User admin = new User();
            admin.setUsername("admin");
            admin.setPasswordInClear("toor");
            admin.setAdmin(true);
            Ebean.save(admin);
        }
    }

    public User getUserByUsername(String username) {
        return Ebean.find(User.class)
                .where().eq("username", username)
                .findOne();
    }


    public void updateUser(User user) {
        Ebean.save(user);
    }
}
