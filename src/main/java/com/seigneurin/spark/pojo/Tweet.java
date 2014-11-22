package com.seigneurin.spark.pojo;

import java.util.Date;

public class Tweet {

    private String user;
    private String text;
    private Date createdAt;
    private String language;

    public Tweet() {
    }

    public Tweet(String user, String text, Date createdAt, String language) {
        super();
        this.user = user;
        this.text = text;
        this.createdAt = createdAt;
        this.language = language;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

}
