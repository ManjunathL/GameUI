package com.mygubbi.game.dashboard.domain;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Created by Shruthi on 2/14/2018.
 */
public class EmailQuoteToLeadSquare
{
    public static final String SENDER_TYPE = "SenderType";
    public static final String SENDER = "Sender";
    public static final String RECIPIENT_TYPE = "RecipientType";
    public static final String RECIPIENT_EMAIL_FIELDS = "RecipientEmailFields";
    public static final String RECIPIENT = "Recipient";
    public static final String EMAIL_TYPE = "EmailType";
    public static final String EMAIL_LIBRARY_NAME = "EmailLibraryName";
    public static final String CONTENT_HTML = "ContentHTML";
    public static final String CONTENT_TEXT = "ContentText";
    public static final String SUBJECT = "Subject";
    public static final String INCLUDE_EMAIL_FOOTER = "IncludeEmailFooter";
    public static final String SCHEDULE = "Schedule";
    public static final String EMAIL_CATEGORY = "EmailCategory";

    private String SenderType;
    private String Sender;
    private String RecipientType;
    private String RecipientEmailFields;
    private String Recipient;
    private String EmailType;
    private String EmailLibraryName;
    private String ContentHTML;
    private String Subject;
    private String IncludeEmailFooter;
    private String Schedule;
    private String EmailCategory;
    private String ContentText;

    public String getSenderType() {
        return SenderType;
    }

    public void setSenderType(String senderType) {
        SenderType = senderType;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        Sender = sender;
    }

    public String getRecipientType() {
        return RecipientType;
    }

    public void setRecipientType(String recipientType) {
        RecipientType = recipientType;
    }

    public String getRecipientEmailFields() {
        return RecipientEmailFields;
    }

    public void setRecipientEmailFields(String recipientEmailFields) {
        RecipientEmailFields = recipientEmailFields;
    }

    public String getRecipient() {
        return Recipient;
    }

    public void setRecipient(String recipient) {
        Recipient = recipient;
    }

    public String getEmailType() {
        return EmailType;
    }

    public void setEmailType(String emailType) {
        EmailType = emailType;
    }

    public String getEmailLibraryName() {
        return EmailLibraryName;
    }

    public void setEmailLibraryName(String emailLibraryName) {
        EmailLibraryName = emailLibraryName;
    }

    public String getContentHTML() {
        return ContentHTML;
    }

    public void setContentHTML(String contentHTML) {
        ContentHTML = contentHTML;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getIncludeEmailFooter() {
        return IncludeEmailFooter;
    }

    public void setIncludeEmailFooter(String includeEmailFooter) {
        IncludeEmailFooter = includeEmailFooter;
    }

    public String getSchedule() {
        return Schedule;
    }

    public void setSchedule(String schedule) {
        Schedule = schedule;
    }

    public String getEmailCategory() {
        return EmailCategory;
    }

    public void setEmailCategory(String emailCategory) {
        EmailCategory = emailCategory;
    }

    public String getContentText() {
        return ContentText;
    }

    public void setContentText(String contentText) {
        ContentText = contentText;
    }


    @Override
    public String toString() {
        return "{" +
                "SenderType:'" + SenderType + '\'' +
                ", Sender:'" + Sender + '\'' +
                ", RecipientType:'" + RecipientType + '\'' +
                ", RecipientEmailFields:'" + RecipientEmailFields + '\'' +
                ", Recipient:'" + Recipient + '\'' +
                ", EmailType='" + EmailType + '\'' +
                ", EmailLibraryName:'" + EmailLibraryName + '\'' +
                ", ContentHTML:'" + ContentHTML + '\'' +
                ", Subject:'" + Subject + '\'' +
                ", IncludeEmailFooter:'" + IncludeEmailFooter + '\'' +
                ", Schedule:'" + Schedule + '\'' +
                ", EmailCategory:'" + EmailCategory + '\'' +
                ", ContentText:'" + ContentText + '\'' +
                '}';
    }
}
