package controllers.twitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.event.Event;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import models.json.twitter.TwitterResponse;
import play.Logger;

import javax.inject.Singleton;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Singleton
public class TwitterRealTimeAPI {

    static private String consumerKey = "";
    static private String consumerSecret = "";
    static private String accessToken = "";
    static private String accessSecret = "";

    private static void loadConfig() {

        HashMap<String, String> configs = TwitterGetConfig.loadConfig();
        consumerKey = configs.get("consumerKey");
        consumerSecret = configs.get("consumerSecret");
        accessToken = configs.get("accessToken");
        accessSecret = configs.get("accessSecret");
    }

    public static void main(String[] args) {

        //load twitter secrets
        loadConfig();

        /** Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream */
        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(100000);
        BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>(1000);

        /** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
        List<Long> followings = Lists.newArrayList(1000088146841952256L);

        hosebirdEndpoint.followings(followings);

        // These secrets should be read from a config file
        Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, accessToken, accessSecret);

        ClientBuilder builder = new ClientBuilder()
                .name("pos-hosebird-Client") // optional: mainly for the logs
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue))
                .eventMessageQueue(eventQueue); // optional: use this if you want to process client events

        Client hosebirdClient = builder.build();
        // Attempts to establish a connection.
        hosebirdClient.connect();

        //create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();

        //hosebirdClient.reconnect();


        // on a different thread, or multiple different threads....
        try {
            while (!hosebirdClient.isDone()) {
                TwitterResponse twitterResponse = objectMapper.readValue(msgQueue.take(), TwitterResponse.class);

                objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
                StringWriter stringTwitterResponse = new StringWriter();
                objectMapper.writeValue(stringTwitterResponse, twitterResponse);
                System.out.println(stringTwitterResponse);
            }
        } catch (Exception e) {
            hosebirdClient.stop();
            Logger.error("hosebirdClient error: "+e);
        }
        Logger.info("end of hosebirdClient...");
    }
}
