package com.androidtutorialshub.countdowntimer.Model;

public class Backup {

    private int key;
    private String filename;
    private int numrows;
    private int timestamp;
    private String notes;

    public Backup() {
    }

    public Backup(int key, String filename, int numrows, int timestamp, String notes) {
        this.key = key;
        this.filename = filename;
        this.numrows = numrows;
        this.timestamp = timestamp;
        this.notes = notes;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getNumrows() {
        return numrows;
    }

    public void setNumrows(int numrows) {
        this.numrows = numrows;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
