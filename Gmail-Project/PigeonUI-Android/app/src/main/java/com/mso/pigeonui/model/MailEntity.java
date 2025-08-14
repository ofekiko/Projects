package com.mso.pigeonui.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "emails", indices = {@Index(value = {"id"}, unique = true)})
public class MailEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "serverMailId")
    private String serverMailId;
    private String title;
    private String content;
    private String sentAt;
    @ColumnInfo(name = "createdDate")
    private String createdDate;
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

    // Constructors:
    public MailEntity() {}

    public MailEntity(String title, String content, String recipientsEmails, boolean toSend) {
        this.title = title;
        this.content = content;
        this.recipientsEmails = recipientsEmails;
        this.toSend = toSend;
    }

    // Getters:
    public int getId() {
        return id;
    }
    public String getServerMailId() { return serverMailId; }
    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
    public String getSentAt() {
        return sentAt;
    }
    public String getCreatedDate() { return createdDate; }
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

    // Setters:
    public void setId(int id) {this.id = id;}
    public void setServerMailId(String serverMailId) {this.serverMailId = serverMailId;}
    public void setTitle(String title) {
        this.title = title;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
    public void setRecipientsEmails(String recipientsEmails) {
        this.recipientsEmails = recipientsEmails;
    }
    public void setToSend(boolean toSend) {
        this.toSend = toSend;
    }
    public void setRead(boolean read) {
        this.read = read;
    }
    public void setBox(String box) {
        this.box = box;
    }
    public void setBlacklisted(boolean blacklisted) {
        isBlacklisted = blacklisted;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    public void setAuthor(String author) {
        this.author = author;
    }
    public void setAuthorFirstName(String authorFirstName) {
        this.authorFirstName = authorFirstName;
    }
    public void setAuthorLastName(String authorLastName) {
        this.authorLastName = authorLastName;
    }
    public void setSenderCopy(boolean senderCopy) {
        isSenderCopy = senderCopy;
    }
}
