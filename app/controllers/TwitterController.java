package controllers;

import middlewares.SessionAuthenticationMiddleware;
import models.*;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import services.EbeanCategoryRepository;
import services.EbeanNoteRepository;
import services.EbeanUserRepository;
import sun.net.www.http.HttpClient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwitterController extends Controller {
    private static final String TAG = "TwitterController";
    private static final String TWITTER_API = "https://api.twitter.com/1.1/favorites/create.json";
    private static final String USER_AGENT = "Mozilla/5.0";

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

    public Result likePost(@Nonnull String postId) {
        boolean likeSuccessful = sendRequestToTwitterAPI(RequestMethod.POST,null,"id="+postId);

        return (likeSuccessful) ? ok("Like successful.") : badRequest("Could not like post with id: "+postId);
    }

    /** @param requestMethod: Enum param (e.g. POST, GET, DELETE, PUT)
     * @param requestProperties: Additional header key-value-pairs.
     * @param urlParameters: What to append to URL? (e.g. id=ksdjlkdsj&ernesto=8545d)
     * @return boolean: Returns whether request was successful or not. */
    public boolean sendRequestToTwitterAPI(@Nonnull RequestMethod requestMethod, @Nullable HashMap<String,String> requestProperties, @Nullable String urlParameters) {
        try {
            URL twitterApiUrl = new URL(TWITTER_API);
            HttpsURLConnection con = (HttpsURLConnection) twitterApiUrl.openConnection();

            //add request header
            con.setRequestMethod(requestMethod.toString());
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language","de-DE,en-US,en;q=0.5");

            //Now also add all request header of hashmap
            if (requestProperties != null) {
                for (Map.Entry<String, String> requestProperty : requestProperties.entrySet()) {
                    con.setRequestProperty(requestProperty.getKey(), requestProperty.getValue());
                }
            }

            //Now add url params
            if (urlParameters != null) {
                con.setDoOutput(true);
                DataOutputStream dos = new DataOutputStream(con.getOutputStream());
                dos.writeBytes(urlParameters);
                dos.flush();
                dos.close();
            }

            int responseCode = con.getResponseCode();
            System.out.println(TAG+":sendRequest: Post-params->"+urlParameters+"\n\tResponse-Code->"+responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println(TAG+":sendRequest: Result->"+response.toString());

            if (responseCode >=400 && responseCode <= 599) {
                return false; //twitterApi returned a unsuccessful errorcode
            }

        } catch (IOException e) {
            System.out.println(TAG+":likePost: Could not send post request.");
            e.printStackTrace();
            return false;
        }
        return true;
    }



}
