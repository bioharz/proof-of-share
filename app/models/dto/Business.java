package models.dto;

import io.ebean.Model;

import javax.annotation.Nonnull;
import java.util.Date;

public class Business extends Model {

    private String businessId; //needed bc. you want to allow the same post published multiple times
    private String postId;
    private String textTwitter;
    private Date date;
    private double btc;
    private double like;

    public Business() {}
    public Business(@Nonnull String businessId, @Nonnull String postId, String textTwitter, Date date, double btc, double like) {
        this.setBusinessId(businessId);
        this.setPostId(postId);
        this.setTextTwitter(textTwitter);
        this.setDate(date);
        this.setDate(date);
        this.setBtc(btc);
        this.setLike(like);
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getTextTwitter() {
        return textTwitter;
    }

    public void setTextTwitter(String textTwitter) {
        this.textTwitter = textTwitter;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getBtc() {
        return btc;
    }

    public void setBtc(double btc) {
        this.btc = btc;
    }

    public double getLike() {
        return like;
    }

    public void setLike(double like) {
        this.like = like;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }
}
