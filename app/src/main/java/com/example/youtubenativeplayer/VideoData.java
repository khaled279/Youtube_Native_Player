package com.example.youtubenativeplayer;

public class VideoData {
    private  String title,describtion,url,id ;
    public VideoData() {
    }

    public VideoData(String title, String describtion, String url, String id) {
        this.title = title;
        this.describtion = describtion;
        this.url = url;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescribtion() {
        return describtion;
    }

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescribtion(String describtion) {
        this.describtion = describtion;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setId(String id) {
        this.id = id;
    }
}
