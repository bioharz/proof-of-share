package controllers;

import controllers.interfaces.ITwitterController;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class TwitterController extends Controller implements ITwitterController {
    private static final String TAG = "TwitterController";
    private static Twitter twitter;
    private FormFactory formFactory;



    public enum RequestMethod {
        POST("POST"),GET("GET"),DELETE("DELETE"),PUT("PUT");

        private final String requestMethodVal;
        RequestMethod(final String requestMethodVal) {
            this.requestMethodVal = requestMethodVal;
        }

        @Override
        public String toString() {
            return requestMethodVal;
        }
    }

    @Inject
    public TwitterController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }



    //METHODS ++++++++++++++++++++++++++++++++++++
    public Result likePost() {
        boolean wasSuccessful = false;
        /* Receive post params */
        final String postId = formFactory.form().bindFromRequest().get("postId");
        String resultMsg = "Unknown error.";

        /* Like post and verify that it has been liked. */
        Status twitterPost = null;
        try {
            twitterPost = TwitterController.getTwitter().createFavorite(Long.parseLong(postId));
            if (twitterPost.isFavorited()) {
                wasSuccessful = true;
                resultMsg = "Post successfully liked: "+postId;
            }
        } catch (TwitterException | NumberFormatException e) {
            //Also catch numberFormatException to avoid people sending randomData to crash our application.
            System.out.println("likePost-> Could not like post with id: "+postId);
            e.printStackTrace();
            resultMsg = "Could not like post with id: "+postId+"\nReason: "+e.getLocalizedMessage();
        }
        return (wasSuccessful) ? ok(resultMsg) : badRequest(resultMsg);
    }



    //GETTER/SETTER ----------------
    public static void setTwitter(Twitter twitter) {
        TwitterController.twitter = twitter;
    }

    public static Twitter getTwitter() {
        if (TwitterController.twitter == null) {
            Twitter twitter = new TwitterFactory().getInstance();
            twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
            twitter.setOAuthAccessToken(new AccessToken(ACCESS_TOKEN, ACCESS_TOKEN_SECRET));
            TwitterController.setTwitter(twitter);
        }
        return TwitterController.twitter;
    }
}
