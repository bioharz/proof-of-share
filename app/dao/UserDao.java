package dao;

import com.google.inject.AbstractModule;
import io.ebean.Ebean;
import models.entities.User;
import play.Logger;

public class UserDao extends AbstractModule {

    @Override
    protected void configure() {
    }

    public User getUserByUsername(String username) {
        try {
            return Ebean.find(User.class)
                    .where().eq("username", username)
                    .findOne();
        } catch (Exception e) {
            Logger.error("Error while try to fetch user with username: " + username + ". Error: " + e);
            return null;
        }
    }

    public User updateUser(User user) {
        Ebean.save(user);
        return user;
    }

    public void deleteUser(int id) {
        Ebean.delete(User.class, id);
    }


    public User getUserByTwitterScreenName(String twitterScreenName){
        try {
            return Ebean.find(User.class)
                    .where().eq("twitterScreenName", twitterScreenName)
                    .findOne();
        } catch (Exception e) {
            Logger.error("Error while try to fetch user with twitterScreenName: " + twitterScreenName + ". Error: " + e);
            return null;
        }
    }
}
