package com.example.linkstonewsbyword;

public class Article{

    private final String title;
    private final String link;
    private final String icon;
    private final String date;

    public Article(String title, String link, String icon, String date) {
        this.icon = icon;
        this.title = title;
        this.link = link;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public String getLink() {
        return link;
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return title;
    }

    @Override
    public String toString() {
        return this.title;
    }
}
