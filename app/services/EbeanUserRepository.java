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
            user1.setUsername("user2");
            user1.setPasswordInClear("password2");
            Ebean.save(user2);
        }
    }

    public User getUserByUsername(String username) {
        return Ebean.find(User.class)
                .where().eq("username", username)
                .findOne();
    }

}
