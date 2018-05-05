package models;

import akka.http.impl.util.JavaMapping;

import java.util.Date;

public class Business {

    private String uri;
    private String textTwitter;
    private Date date;
    private double btc;
    private double like;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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
}
