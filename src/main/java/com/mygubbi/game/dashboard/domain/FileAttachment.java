package com.mygubbi.game.dashboard.domain;

/**
 * Created by nitinpuri on 01-05-2016.
 */
public class FileAttachment {
    private String fileName;
    private String title;
    private String uploadPath;

    public FileAttachment(String fileName, String title, String uploadPath) {
        this.fileName = fileName;
        this.title = title;
        this.uploadPath = uploadPath;
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

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }
}
