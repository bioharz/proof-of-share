package dao;

import io.ebean.Ebean;
import models.entities.TwitterBountyCampaign;
import models.entities.TwitterBountyCampaign;
import models.entities.User;
import play.Logger;

import java.util.List;

public class TwitterBountyCampaignDao {

    @Deprecated
    public List<TwitterBountyCampaign> getTweets() {
        return Ebean.find(TwitterBountyCampaign.class).findList();
    }


    public List<TwitterBountyCampaign> getTweets(User user) {
        if (user.getAdmin()) {
            return Ebean.find(TwitterBountyCampaign.class)
                    .where()
                    .findList();
        } else {
            return Ebean.find(TwitterBountyCampaign.class)
                    .where()
                    .eq("user", user)
                    .findList();
        }
    }

    @Deprecated
    public TwitterBountyCampaign getTweet(int id) {
        return Ebean.find(TwitterBountyCampaign.class)
                .where()
                .eq("id", id)
                .findOne();
    }

    public TwitterBountyCampaign getTweet(int id, User user) {
        if (user.getAdmin()) {
            return Ebean.find(TwitterBountyCampaign.class)
                    .where()
                    .eq("id", id)
                    .findOne();
        } else {
            return Ebean.find(TwitterBountyCampaign.class)
                    .where()
                    .eq("id", id)
                    .eq("user", user)
                    .findOne();
        }
    }

    @Deprecated
    public void saveTweets(TwitterBountyCampaign TwitterBountyCampaign) {
        TwitterBountyCampaign.setLastEdited((int) (System.currentTimeMillis() / 1000L));

        if (TwitterBountyCampaign.getId() > 0) {
            Ebean.update(TwitterBountyCampaign);
        } else {
            Ebean.save(TwitterBountyCampaign);
        }
    }

    public void saveTweets(TwitterBountyCampaign TwitterBountyCampaign, User user) {

        TwitterBountyCampaign.setLastEdited((int) (System.currentTimeMillis() / 1000L));
        if (user.getAdmin()) {
            if (TwitterBountyCampaign.getId() > 0) {
                Ebean.update(TwitterBountyCampaign);
            } else {
                Ebean.save(TwitterBountyCampaign);
            }
        } else {
            if (TwitterBountyCampaign.getId() > 0) {
                TwitterBountyCampaign noteOld = getTweet(TwitterBountyCampaign.getId());
                if (noteOld.getUser().equals(user)) { //TODO: I have no clue if this will work
                    Ebean.update(TwitterBountyCampaign);
                } else {
                    Logger.error("Don't get me wrong, but i think your code is broken. See TwitterBountyCampaignDao, saveTweets(id, user)");
                }
            } else {
                TwitterBountyCampaign.setUser(user);
                Ebean.save(TwitterBountyCampaign);
            }
        }
    }

    @Deprecated
    public void deleteTweet(int id) {
        Ebean.delete(TwitterBountyCampaign.class, id);
    }

    public void deleteTweet(int id, User user) {
        if (user != null) {
            TwitterBountyCampaign TwitterBountyCampaign = getTweet(id);
            if (TwitterBountyCampaign.getUser().equals(user)) { //TODO: I have no clue if this will work
                Ebean.delete(TwitterBountyCampaign.class, id);
            } else {
                Logger.error("Don't get me wrong, but i think your code is broken. See TwitterBountyCampaignDao, deleteTweet(id, user)");
            }
        }
    }

}
