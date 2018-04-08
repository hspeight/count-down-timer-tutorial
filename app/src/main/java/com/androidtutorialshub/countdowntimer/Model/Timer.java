package com.androidtutorialshub.countdowntimer.Model;

/**
 * Created by hspei on 28/03/2018.
 */

public class Timer {

    private int key;
    private String title;
    private String desc;
    private String message;
    private int timestamp;
    private String image;
    private String status;
    private String type;

    public Timer() {
    }

    public Timer(int key, String title, String desc, String message, int timestamp, String image, String status, String type) {
        this.key = key;
        this.title = title;
        this.desc = desc;
        this.message = message;
        this.timestamp = timestamp;
        this.image = image;
        this.status = status;
        this.type = type;
    }

    public Timer(String title, String desc,  String message, int timestamp, String image, String status, String type) {
        this.title = title;
        this.desc = desc;
        this.message = message;
        this.timestamp = timestamp;
        this.image = image;
        this.status = status;
        this.type = type;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
