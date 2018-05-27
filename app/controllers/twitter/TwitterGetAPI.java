package controllers.twitter;

import play.Logger;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

import java.util.HashMap;
import java.util.List;

public class TwitterGetAPI {

    private Twitter twitterInstance = null;

    public TwitterGetAPI() {
        getTwitterInstance();
    }

    private void getTwitterInstance() {
        HashMap<String, String> configs = TwitterGetConfig.loadConfig();
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(configs.get("consumerKey"))
                .setOAuthConsumerSecret(configs.get("consumerSecret"))
                .setOAuthAccessToken(configs.get("accessToken"))
                .setOAuthAccessTokenSecret(configs.get("accessSecret"))
                .setIncludeMyRetweetEnabled(true);
        TwitterFactory twitterFactory = new TwitterFactory(cb.build());
        twitterInstance = twitterFactory.getInstance();
    }

    public List<Status> getRetweetStatusList(List<Long> statusesLongList) {
        //only the first 100 reTweets....
        try {
            List<Status> statuses = null;
            if (statusesLongList.isEmpty()) {
                return statuses;
            }
            statuses = twitterInstance.getRetweets(statusesLongList.get(0));
            if (statusesLongList.size() > 1) {
                for (int i = 1; i < statusesLongList.size(); i++) {
                    statuses.addAll(twitterInstance.getRetweets(statusesLongList.get(i)));
                }
            }
        } catch (Exception e) {
            Logger.error("Failed to get retweets: " + e);
        }
        return null;
    }

    public List<Status> getLikeStatusList(List<Long> statusesLongList) {
        try {
            List<Status> statuses = null;
            if (statusesLongList.isEmpty()) {
                return statuses;
            }
            statuses = twitterInstance.getFavorites(statusesLongList.get(0));
            if (statusesLongList.size() > 1) {
                for (int i = 1; i < statusesLongList.size(); i++) {
                    statuses.addAll(twitterInstance.getFavorites(statusesLongList.get(i)));
                }
            }
        } catch (Exception e) {
            Logger.error("Failed to get retweets: " + e);
        }
        return null;
    }

    public Status getStatus(long statusId) {
        try {
            return twitterInstance.showStatus(statusId);
        } catch (Exception e) {
            Logger.error("Failed to getStatus: " + e);
        }
        return null;
    }

    public User getUser(String twitterScreenName) {
        try {
            return twitterInstance.showUser(twitterScreenName);
        } catch (Exception e) {
            Logger.error("Failed to getStatus: " + e);
        }
        return null;
    }
}
