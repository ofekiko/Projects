package com.mso.pigeonui.model;
public class MailRequest {
    private final String title;
    private final String content;
    private final String[] recipientsEmails;
    private final boolean toSend;

    public MailRequest(String title, String content, String[] recipientsEmails, boolean toSend) {
        this.title = title;
        this.content = content;
        this.recipientsEmails = recipientsEmails;
        this.toSend = toSend;
    }

    // Getters:
    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
    public String[] getRecipientsEmails() {
        return recipientsEmails;
    }
    public boolean isToSend() {
        return toSend;
    }
}