package com.mygubbi.game.dashboard.domain;

import java.util.Date;

/**
 * Created by nitinpuri on 01-05-2016.
 */
public class FileAttachment {

    public static final String ID = "id";
    public static final String SEQ = "seq";
    public static final String TITLE = "title";
    public static final String FILENAME = "fileName";
    public static final String UPLOADED_BY = "uploadedBy";
    public static final String UPLOADED_ON = "uploadedOn";

    private int id;
    private int seq;
    private String title;
    private String fileName;
    private String uploadedBy;
    private Date uploadedOn;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public Date getUploadedOn() {
        return uploadedOn;
    }

    public void setUploadedOn(Date uploadedOn) {
        this.uploadedOn = uploadedOn;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileAttachment that = (FileAttachment) o;

        if (seq != that.seq) return false;
        if (!title.equals(that.title)) return false;
        if (!fileName.equals(that.fileName)) return false;
        if (!uploadedBy.equals(that.uploadedBy)) return false;
        return uploadedOn.equals(that.uploadedOn);

    }

    @Override
    public int hashCode() {
        int result = seq;
        result = 31 * result + title.hashCode();
        result = 31 * result + fileName.hashCode();
        result = 31 * result + uploadedBy.hashCode();
        result = 31 * result + uploadedOn.hashCode();
        return result;
    }
}
