package com.example.covid_19_tracker.model;

public class Update {

    private String news;
    private String source;

    public Update(String news, String source) {
        this.news = news;
        this.source = source;
    }

    public String getNews() {
        return news;
    }

    public void setNews(String news) {
        this.news = news;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
