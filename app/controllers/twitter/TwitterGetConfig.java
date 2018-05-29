package controllers.twitter;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.HashMap;
import java.util.List;

public class TwitterGetConfig {

    protected static HashMap<String, String> loadConfig() {

        Config config = ConfigFactory.load("twitter").getConfig("twitter-secrets");

        HashMap<String, String> configs = new HashMap<>();
        configs.put("consumerKey", config.getString("CONSUMER_KEY"));
        configs.put("consumerSecret", config.getString("CONSUMER_SECRET"));
        configs.put("accessToken", config.getString("ACCESS_TOKEN"));
        configs.put("accessSecret", config.getString("ACCESS_SECRET"));
        return configs;
    }
}
