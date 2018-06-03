package models.entities;

import io.ebean.Model;
import models.interfaces.validation.TwitterBountyCampaignCheck;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Constraints.Validate(groups = {TwitterBountyCampaignCheck.class})
public class TwitterBountyCampaign extends Model implements Constraints.Validatable<List<ValidationError>> {

    @Id
    @NotNull
    protected int id; // = 0;

    @NotNull
    @Constraints.Required(groups = {TwitterBountyCampaignCheck.class})
    protected long tweetId;

    protected String twitterScreenName;

    //@NotNull
    @Constraints.Min(groups = {TwitterBountyCampaignCheck.class}, value = 1, message = "Please spend at least 1 Satoshis per reTweet")
    @Constraints.Max(groups = {TwitterBountyCampaignCheck.class}, value = 10000, message = "Only a total amount of 10000 Satoshis were allowed per reTweet")
    protected long satoshiPerReTweet;// = 0;

    //@NotNull
    @Constraints.Min(groups = {TwitterBountyCampaignCheck.class}, value = 1, message = "Please spend at least 1 Satoshis per like")
    @Constraints.Max(groups = {TwitterBountyCampaignCheck.class}, value = 10000, message = "Only a total amount of 10000 Satoshis were allowed per like")
    protected long satoshiPerLike;// = 0;

    @NotNull
    @Constraints.Required(groups = {TwitterBountyCampaignCheck.class})
    @Constraints.Min(groups = {TwitterBountyCampaignCheck.class}, value = 10000, message = "Please spend at least 10000 Satoshis on that campaign")
    @Constraints.Max(groups = {TwitterBountyCampaignCheck.class}, value = 1000000, message = "Only a total amount of 1000000 Satoshis were allowed per campaign")
    protected long totalSatoshiToSpend;

    protected int created;

    protected boolean disabled = false;

    @ManyToOne
    protected User user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTweetId() {
        return tweetId;
    }

    public void setTweetId(long tweetId) {
        this.tweetId = tweetId;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public String getCreatedFormatted() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(this.created * 1000L));
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getSatoshiPerReTweet() {
        return satoshiPerReTweet;
    }

    public void setSatoshiPerReTweet(long satoshiPerReTweet) {
        this.satoshiPerReTweet = satoshiPerReTweet;
    }

    public long getSatoshiPerLike() {
        return satoshiPerLike;
    }

    public void setSatoshiPerLike(long satoshiPerLike) {
        this.satoshiPerLike = satoshiPerLike;
    }

    public long getTotalSatoshiToSpend() {
        return totalSatoshiToSpend;
    }

    public void setTotalSatoshiToSpend(long totalSatoshiToSpend) {
        this.totalSatoshiToSpend = totalSatoshiToSpend;
    }

    public String getTwitterScreenName() {
        return twitterScreenName;
    }

    public void setTwitterScreenName(String twitterScreenName) {
        this.twitterScreenName = twitterScreenName;
    }

    @Override
    public List<ValidationError> validate() {
        List<ValidationError> validationErrorList = new ArrayList<>();
        if ((satoshiPerLike == 0) && (satoshiPerReTweet == 0)) {
            String errorMessage = "Satoshi per like OR Satoshi per retweet must have at least a valid value.";
            validationErrorList.add(new ValidationError("satoshiPerLike", errorMessage));
            validationErrorList.add(new ValidationError("satoshiPerReTweet", errorMessage));
        }
        if (totalSatoshiToSpend >= (satoshiPerReTweet + satoshiPerLike)) {
            String errorMessage = "Total amount of campaigns Satohis have to be greater than Satoshi per retweet and Satoshi per like together.";
            validationErrorList.add(new ValidationError("totalSatoshiToSpend", errorMessage));
            validationErrorList.add(new ValidationError("satoshiPerLike", errorMessage));
            validationErrorList.add(new ValidationError("satoshiPerReTweet", errorMessage));
        }
        return validationErrorList;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
