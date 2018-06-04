package scheduler;

import akka.actor.ActorSystem;
import com.github.tuxBurner.jobs.AbstractAnnotatedJob;
import com.github.tuxBurner.jobs.AkkaJob;
import com.github.tuxBurner.jobs.JobException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import controllers.twitter.TwitterGetAPI;
import dao.TwitterBountyCampaignDao;
import dao.UserDao;
import models.entities.TwitterBountyCampaign;
import models.entities.User;
import play.Logger;
import twitter4j.Status;

import java.util.ArrayList;
import java.util.List;

/**
 * Job is fired every 15 Seconds
 */
@AkkaJob(cronExpression = "0/15 * * * * ?")
public class TwitterGetScheduler extends AbstractAnnotatedJob {

    protected TwitterGetAPI twitterGetAPI;
    protected TwitterBountyCampaignDao twitterBountyCampaignDao;
    protected UserDao userDao;

    public TwitterGetScheduler(ActorSystem actorSystem) throws JobException {
        super(actorSystem);

        //TODO: QUICK AND DIRTY injector. Please do some refactoring
        Injector injector = Guice.createInjector(new TwitterGetAPI(), new TwitterBountyCampaignDao(), new UserDao());

        twitterGetAPI = injector.getInstance(TwitterGetAPI.class);
        twitterBountyCampaignDao = injector.getInstance(TwitterBountyCampaignDao.class);
        userDao = injector.getInstance(UserDao.class);
    }

    @Override
    public void runInternal() {

        List<TwitterBountyCampaign> twitterBountyCampaigns = new ArrayList<>();

        twitterBountyCampaigns = twitterBountyCampaignDao.getAllActiveTweetsWithFund();

        if (!twitterBountyCampaigns.isEmpty()) {
            for (TwitterBountyCampaign twitterBountyCampaign : twitterBountyCampaigns) {
                if (campaignHaveEnoughToPay(twitterBountyCampaign)) {

                } else {
                    disableCampaign(twitterBountyCampaign);
                }
            }
        }

        Logger.debug("Run run");
        List<Long> testStatusIds = new ArrayList<>();
        testStatusIds.add(1000754697278513152L);
        testStatusIds.add(1000708329914667008L);
        testStatusIds.add(1000322992919007232L);
        List<Status> twitterStatusList = twitterGetAPI.getRetweetStatusList(testStatusIds);

        for (Status status : twitterStatusList) {
            Logger.info("Tweet-text: " + status.getText() + "\n" +
                    "RetweetCount: " + status.getRetweetCount() + "\n" +
                    "FavoriteCount: " + status.getFavoriteCount());
        }
    }


    private boolean campaign(TwitterBountyCampaign twitterBountyCampaign){



        return false;
    }


    //Yeah. It's a and. But for now, it's OK
    private boolean campaignHaveEnoughToPay(TwitterBountyCampaign twitterBountyCampaign) {
        return (twitterBountyCampaign.getTotalSatoshiToSpend() >= twitterBountyCampaign.getSatoshiPerLike() &&
                twitterBountyCampaign.getTotalSatoshiToSpend() >= twitterBountyCampaign.getSatoshiPerReTweet());

    }

    //TODO: duplicate code :(
    private boolean disableCampaign(TwitterBountyCampaign twitterBountyCampaign) {
        try {
            User user = twitterBountyCampaign.getUser();
            int id = twitterBountyCampaign.getId();
            if (user != null) {
                if (!twitterBountyCampaign.isDisabled()) {
                    boolean campaignStopped = twitterBountyCampaignDao.stopTweet(id, user);
                    if (campaignStopped) {
                        long oldBalance = user.getSatoshiBalance();
                        long newBalance = oldBalance + twitterBountyCampaign.getTotalSatoshiToSpend();

                        twitterBountyCampaign.setTotalSatoshiToSpend(0L);
                        boolean campaignSatoshiSetToZero = twitterBountyCampaignDao.setSatoshiToZero(id, user);
                        if (campaignSatoshiSetToZero) {
                            if (twitterBountyCampaignDao.getTweet(twitterBountyCampaign.getId(), user).getTotalSatoshiToSpend() == 0L) {
                                user.setSatoshiBalance(newBalance);
                                User newTempUser = userDao.updateUser(user);
                                if (newTempUser != null && newTempUser.getSatoshiBalance() == newBalance) {
                                    Logger.info("success", "campaign successfully stopped.");
                                    return true;
                                }
                            }
                        }
                    }
                }
                Logger.error("Can't stop the campaign in scheduler.");
            }
        } catch (Exception e) {
            Logger.error("Exception while try to sop the campaign in scheduler: "+e);
        }
        return false;
    }

}