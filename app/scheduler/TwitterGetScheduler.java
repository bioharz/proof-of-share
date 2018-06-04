package scheduler;

import akka.actor.ActorSystem;
import com.github.tuxBurner.jobs.AbstractAnnotatedJob;
import com.github.tuxBurner.jobs.AkkaJob;
import com.github.tuxBurner.jobs.JobException;
import controllers.twitter.TwitterGetAPI;
import play.Logger;
import twitter4j.Status;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple job which is fired every 15 Seconds
 */
@AkkaJob(cronExpression = "0/15 * * * * ?")
public class TwitterGetScheduler extends AbstractAnnotatedJob {


    @Inject
    protected TwitterGetAPI twitterGetAPI;

    public TwitterGetScheduler(ActorSystem actorSystem) throws JobException {
        super(actorSystem);
    }

    @Override
    public void runInternal() {
        Logger.debug("Run run");
        List<Long> testStatusIds = new ArrayList<>();
        testStatusIds.add(1000754697278513152L);
        testStatusIds.add(1000708329914667008L);
        testStatusIds.add(1000322992919007232L);
        List<Status> twitterStatusList = twitterGetAPI.getLikeStatusList(testStatusIds);

        for (Status status : twitterStatusList) {
            Logger.info("Tweet-text: " + status.getText() + "\n" +
                        "RetweetCount: " + status.getFavoriteCount()+ "\n" +
                        "FavoriteCount: " + status.getFavoriteCount());
        }
    }
}