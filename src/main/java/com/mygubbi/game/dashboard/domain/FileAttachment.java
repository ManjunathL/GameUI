package com.mygubbi.game.dashboard.domain;

import java.util.Date;

/**
 * Created by nitinpuri on 01-05-2016.
 */
public class FileAttachment {
    private int id;
    private String title;
    private String fileName;
    private String uploadedBy;
    private Date uploadedOn;

    public FileAttachment(String fileName, String title) {
        this.fileName = fileName;
        this.title = title;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
