package com.mso.pigeonui.model; // Or com.mso.pigeonui.model.network

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MailApiResponse {

    @SerializedName("_id")
    private String id;
    private String title;
    private String content;

    @SerializedName("sentAt")
    private String sentAt;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    private String userId;
    private String authorId;
    private String author;
    private String authorFirstName;
    private String authorLastName;
    private List<String> recipientsEmails;
    private boolean toSend;
    private boolean read;
    private String box;
    private boolean isBlacklisted;
    private boolean isSenderCopy;

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getSentAt() { return sentAt; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public String getUserId() { return userId; }
    public String getAuthorId() { return authorId; }
    public String getAuthor() { return author; }
    public String getAuthorFirstName() { return authorFirstName; }
    public String getAuthorLastName() { return authorLastName; }
    public List<String> getRecipientsEmails() { return recipientsEmails; }
    public boolean isToSend() { return toSend; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public String getBox() { return box; }
    public void setBox(String box) { this.box = box; }
    public boolean isBlacklisted() { return isBlacklisted; }
    public boolean isSenderCopy() { return isSenderCopy; }
    public MailApiResponse() {}

    @Override
    public String toString() {
        return "MailApiResponse{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", read=" + read +
                ", box='" + box + '\'' +
                '}';
    }
}