package scheduler;

import akka.actor.ActorSystem;
import com.github.tuxBurner.jobs.AbstractAnnotatedJob;
import com.github.tuxBurner.jobs.AkkaJob;
import com.github.tuxBurner.jobs.JobException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import controllers.twitter.TwitterGetAPI;
import play.Logger;
import twitter4j.Status;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple job which is fired every 15 Seconds
 */
@AkkaJob(cronExpression = "0/15 * * * * ?")
public class TwitterGetScheduler extends AbstractAnnotatedJob {

    protected TwitterGetAPI twitterGetAPI;

    public TwitterGetScheduler(ActorSystem actorSystem) throws JobException {
        super(actorSystem);

        Injector injector = Guice.createInjector(new TwitterGetAPI());

        twitterGetAPI = injector.getInstance(TwitterGetAPI.class);

    }

    @Override
    public void runInternal() {
        Logger.debug("Run run");
        List<Long> testStatusIds = new ArrayList<>();
        testStatusIds.add(1000754697278513152L);
        testStatusIds.add(1000708329914667008L);
        testStatusIds.add(1000322992919007232L);
        List<Status> twitterStatusList = twitterGetAPI.getRetweetStatusList(testStatusIds);

        for (Status status : twitterStatusList) {
            Logger.info("Tweet-text: " + status.getText() + "\n" +
                        "RetweetCount: " + status.getRetweetCount() + "\n" +
                        "FavoriteCount: " + status.getFavoriteCount() + "\n" +
            );
        }
    }
}