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

    /** Kanns leider nicht kompilieren (einige Bibliotheken fehlen usw.
     * Nach secrets usw hinzufügen hab ich dir hier meine Vorschläge kommentiert.
     * Einer müsste normalerweise funktionieren. In absteigender Reihenfolge
     * meine Empfehlungen:
     *
     * ######################### 1 ######################
     * protected TwitterGetAPI twitterGetAPI;
     * public TwitterGetScheduler(ActorSystem actorSystem, @Inject TwitterGetApi twitterGetAPI) throws JobException {
     *     super(actorSystem);
     *     this.twitterGetAPI = twitterGetAPI;
     * }
     *
     * ######################### 2 ###################### --> so hast dus
     * @Inject
     * protected TwitterGetAPI twitterGetAPI;
     * public TwitterGetScheduler(ActorSystem actorSystem) throws JobException {
     *     super(actorSystem);
     * }
     *
     * ######################### 3 ######################
     * protected TwitterGetAPI twitterGetAPI;
     * @Inject
     * public TwitterGetScheduler(ActorSystem actorSystem, TwitterGetApi twitterGetAPI) throws JobException {
     *     super(actorSystem);
     *     this.twitterGetAPI = twitterGetAPI;
     * }
     *
     * Ansonsten keine Ahnung warum. Am besten zeigst mir das Problem mal auf deinem Laptop, bevor ich mich hier
     * stundenlang aufrege, um mal alle Bibliotheken zu importieren usw.
     *
     * */

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