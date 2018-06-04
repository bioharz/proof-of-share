package dao;

import com.google.inject.AbstractModule;
import io.ebean.Ebean;
import models.entities.TwitterBountyCampaign;
import models.entities.User;
import play.Logger;

import java.util.List;

public class TwitterBountyCampaignDao extends AbstractModule {

    @Override
    protected void configure() {
    }

    @Deprecated
    public List<TwitterBountyCampaign> getTweets() {
        return Ebean.find(TwitterBountyCampaign.class).findList();
    }


    public TwitterBountyCampaign getTweetByTweetId(long tweetId) {
        return Ebean.find(TwitterBountyCampaign.class)
                .where()
                .eq("tweetId", tweetId)
                .findOne();
    }

    public TwitterBountyCampaign getActiveTweetByTweetId(long tweetId) {
        return Ebean.find(TwitterBountyCampaign.class)
                .where()
                .eq("tweetId", tweetId)
                .eq("disabled", false)
                .findOne();
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
    public boolean saveTweets(TwitterBountyCampaign twitterBountyCampaign) {
        twitterBountyCampaign.setCreated((int) (System.currentTimeMillis() / 1000L));

        try {
            if (twitterBountyCampaign.getId() > 0) {
                //TODO: we should handle this in a different way..
                //Logger.error("for now, we can't edit a running campaign");
                //return false;
                Ebean.update(twitterBountyCampaign);
            } else {
                Ebean.save(twitterBountyCampaign);
            }
            return true;
        } catch (Exception e) {
            Logger.error("error while saving a new twitter: " + e);
            return false;
        }
    }

    public boolean saveTweets(TwitterBountyCampaign twitterBountyCampaign, User user) {

        try {

            if (user.getAdmin()) {
                if (twitterBountyCampaign.getId() > 0) {
                    Ebean.update(twitterBountyCampaign);
                } else {
                    twitterBountyCampaign.setCreated((int) (System.currentTimeMillis() / 1000L));
                    Ebean.save(twitterBountyCampaign);
                }
            } else {
                if (twitterBountyCampaign.getId() > 0) {
                    TwitterBountyCampaign noteOld = getTweet(twitterBountyCampaign.getId());
                    if (noteOld.getUser().equals(user)) { //TODO: I have no clue if this will work
                        Ebean.update(twitterBountyCampaign);
                    } else {
                        Logger.error("Don't get me wrong, but i think your code is broken. See TwitterBountyCampaignDao, saveTweets(id, user)");
                    }
                } else {
                    twitterBountyCampaign.setCreated((int) (System.currentTimeMillis() / 1000L));
                    twitterBountyCampaign.setUser(user);
                    Ebean.save(twitterBountyCampaign);
                }
            }
            return true;
        } catch (Exception e) {
            Logger.error("error while saving a new twitter: " + e);
            return false;
        }

    }

    @Deprecated
    public void deleteTweet(int id) {
        Ebean.delete(TwitterBountyCampaign.class, id);
    }

    public void deleteTweet(int id, User user) {
        if (user != null) {
            TwitterBountyCampaign twitterBountyCampaign = getTweet(id);
            if (twitterBountyCampaign.getUser().equals(user)) { //TODO: I have no clue if this will work
                Ebean.delete(TwitterBountyCampaign.class, id);
            } else {
                Logger.error("Don't get me wrong, but i think your code is broken. See TwitterBountyCampaignDao, deleteTweet(id, user)");
            }
        }
    }


    public boolean stopTweet(int id, User user) {
        try {
            if (user != null) {
                TwitterBountyCampaign twitterBountyCampaign = getTweet(id, user);
                if (twitterBountyCampaign != null) {
                    twitterBountyCampaign.setDisabled(true);
                    Ebean.update(twitterBountyCampaign);
                    return true;
                }
            }
        } catch (Exception e) {
            Logger.error("Can't disable bounty campaign: " + e);
        }
        return false;
    }


    public boolean stopTweet(int id) {
        try {
            TwitterBountyCampaign twitterBountyCampaign = getTweet(id);
            if (twitterBountyCampaign != null) {
                twitterBountyCampaign.setDisabled(true);
                Ebean.update(twitterBountyCampaign);
                return true;
            }
        } catch (Exception e) {
            Logger.error("Can't disable bounty campaign: " + e);
        }
        return false;
    }

    public boolean setSatoshiToZero(int id, User user) {
        try {
            if (user != null) {
                TwitterBountyCampaign twitterBountyCampaign = getTweet(id, user);
                if (twitterBountyCampaign != null) {
                    twitterBountyCampaign.setTotalSatoshiToSpend(0L);
                    Ebean.update(twitterBountyCampaign);
                    return true;
                }
            }
        } catch (Exception e) {
            Logger.error("Can't set Satoshi to zero: " + e);
        }
        return false;
    }

    public boolean atleastOneActiveCampaign() {
        try {
            TwitterBountyCampaign twitterBountyCampaign = Ebean.find(TwitterBountyCampaign.class)
                    .where()
                    .eq("disabled", false)
                    .findOne();
            if (twitterBountyCampaign != null) {
                return true;
            }
        } catch (Exception e) {
            Logger.error("anyActiveCampaign error: " + e);

        }
        return false;
    }


    public List<TwitterBountyCampaign> getAllActiveTweets() {
        return Ebean.find(TwitterBountyCampaign.class)
                .where()
                .eq("disabled", false)
                .findList();
    }

    public List<TwitterBountyCampaign> getAllActiveTweetsWithFund() {
        return Ebean.find(TwitterBountyCampaign.class)
                .where()
                .eq("disabled", false)
                .gt("totalSatoshiToSpend", 0)
                .findList();
    }

}
