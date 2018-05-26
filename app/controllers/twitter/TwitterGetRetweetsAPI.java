package controllers.twitter;

import play.Logger;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.api.TweetsResources;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TwitterGetRetweetsAPI {

    static private String consumerKey = "";
    static private String consumerSecret = "";
    static private String accessToken = "";
    static private String accessSecret = "";

    private static void loadConfig() {
        HashMap<String, String> configs = TwitterGetConfig.loadConfig();
        consumerKey = configs.get(consumerKey);
        consumerSecret = configs.get(consumerSecret);
        accessToken = configs.get(accessToken);
        accessSecret = configs.get(accessSecret);
    }

    private static ConfigurationBuilder buildConfigure() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessSecret)
                .setIncludeMyRetweetEnabled(true);
        return cb;
    }

    public static List<Status> getRetweetStatusList(List<Long> statusesLongList) {
        loadConfig();
        buildConfigure();

        //only the first 100 reTweets....
        try {
            TwitterFactory tf = new TwitterFactory(buildConfigure().build());
            Twitter twitter = tf.getInstance();
            //Twitter twitter = new TwitterFactory().getInstance();
            List<Status> statuses = null;
            if (statusesLongList.isEmpty()) {
                return statuses;
            }
            statuses = twitter.getRetweets(statusesLongList.get(0));
            if (statusesLongList.size() > 1) {
                for (int i = 1; i < statusesLongList.size(); i++) {
                    statuses.addAll(twitter.getRetweets(statusesLongList.get(i)));
                }
            }
            for (Status status : statuses) {
                System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
            }

        } catch (Exception e) {
            Logger.error("Failed to get retweets: " + e);
        }
        return null;
    }

}
