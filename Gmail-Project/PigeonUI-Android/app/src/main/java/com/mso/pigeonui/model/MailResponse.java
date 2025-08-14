package com.mso.pigeonui.model;

public class MailResponse {
    // Fields
    private int id;
    private String title;
    private String content;
    private String sentAt;
    private String userId;
    private String authorId;
    private String author;
    private String authorFirstName;
    private String authorLastName;
    private String recipientsEmails;
    private boolean toSend;
    private boolean read;
    private String box;
    private boolean isBlacklisted;
    private boolean isSenderCopy;


    // Getters
    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
    public String getSentAt() {
        return sentAt;
    }
    public String getUserId() {
        return userId;
    }
    public String getAuthorId() { return authorId; }
    public String getAuthor() {
        return author;
    }
    public String getAuthorFirstName() {
        return authorFirstName;
    }
    public String getAuthorLastName() {
        return authorLastName;
    }
    public String getRecipientsEmails() {
        return recipientsEmails;
    }
    public boolean isToSend() {
        return toSend;
    }
    public boolean isRead() {
        return read;
    }
    public String getBox() {
        return box;
    }
    public boolean isBlacklisted() {
        return isBlacklisted;
    }
    public boolean isSenderCopy() {
        return isSenderCopy;
    }
}
