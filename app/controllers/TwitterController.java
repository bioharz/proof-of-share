package controllers;

import com.google.common.base.Strings;
import controllers.interfaces.ITwitterController;
import play.api.libs.oauth.ConsumerKey;
import play.api.libs.oauth.ServiceInfo;
import play.api.libs.ws.WSClient;
import play.data.FormFactory;
import play.libs.oauth.OAuth;
import play.mvc.Controller;
import play.mvc.Result;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import javax.inject.Inject;

import java.util.Optional;

public class TwitterController extends Controller implements ITwitterController {
    private static final String TAG = "TwitterController";
    private static Twitter twitter;
    private FormFactory formFactory;

    /* OAuth Members */
    //TODO: Get keys from twitter.conf
    private static final OAuth.ConsumerKey OAUTH_CONSUMER_KEY = new OAuth.ConsumerKey(ITwitterController.CONSUMER_KEY, CONSUMER_SECRET);
    private static final OAuth.ServiceInfo OAUTH_SERVICE_INFO = new OAuth.ServiceInfo("https://api.twitter.com/oauth/request_token",
            "https://api.twitter.com/oauth/access_token",
            "https://api.twitter.com/oauth/authorize", OAUTH_CONSUMER_KEY);
    private static final OAuth TWITTER_OAUTH = new OAuth(OAUTH_SERVICE_INFO);
    private final WSClient ws;


    public enum RequestMethod {
        POST("POST"), GET("GET"), DELETE("DELETE"), PUT("PUT");

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
    public TwitterController(FormFactory formFactory, WSClient ws) {
        this.formFactory = formFactory;
        this.ws = ws;
    }

    public Result oAuth() {
        System.out.println("oAuth: Starting");
        String verifier = request().getQueryString("oauth_verifier");
        if (Strings.isNullOrEmpty(verifier)) {
            String url = routes.TwitterController.oAuth().absoluteURL(request());
            OAuth.RequestToken requestToken = TWITTER_OAUTH.retrieveRequestToken(url);
            saveSessionTokenPair(requestToken);
            System.out.println("oAuth: redirect 1");
            return redirect(TWITTER_OAUTH.redirectUrl(requestToken.token));
        } else {
            OAuth.RequestToken requestToken = getSessionTokenPair().get();
            OAuth.RequestToken accessToken = TWITTER_OAUTH.retrieveAccessToken(requestToken, verifier);
            saveSessionTokenPair(accessToken);
            System.out.println("oAuth: redirect 2");
            return redirect(routes.BusinessController.like());
        }
    }

    private void saveSessionTokenPair(OAuth.RequestToken requestToken) {
        session("token", requestToken.token);
        session("secret", requestToken.secret);
    }

    private Optional<OAuth.RequestToken> getSessionTokenPair() {
        if (session().containsKey("token")) {
            return Optional.ofNullable(new OAuth.RequestToken(session("token"), session("secret")));
        }
        return Optional.empty();
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
                resultMsg = "Post successfully liked: " + postId;
            }
        } catch (TwitterException | NumberFormatException e) {
            //Also catch numberFormatException to avoid people sending randomData to crash our application.
            System.out.println("likePost-> Could not like post with id: " + postId);
            e.printStackTrace();
            resultMsg = "Could not like post with id: " + postId + " - Reason: " + e.getLocalizedMessage();
        }
        resultMsg = "{\"msg\":\"" + resultMsg.replace("\n", "").replace("\r", "") + "\"}";
        System.out.println(resultMsg);
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
