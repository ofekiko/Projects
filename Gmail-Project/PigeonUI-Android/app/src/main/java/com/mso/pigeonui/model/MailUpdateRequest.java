package com.mso.pigeonui.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
public class MailUpdateRequest {

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("toSend")
    private Boolean toSend;

    @SerializedName("read")
    private Boolean read;

    @SerializedName("box")
    private String box;

    public MailUpdateRequest() {
    }

    public MailUpdateRequest setRead(Boolean read) {
        this.read = read;
        return this;
    }

    public MailUpdateRequest setBox(String box) {
        this.box = box;
        return this;
    }

    public MailUpdateRequest setContent(String content) {
        if (this.toSend == false){
            this.content = content;
        }
        return this;
    }


    public MailUpdateRequest setTitle(String title){
        if (this.toSend == false){
            this.title = title;
        }
        return this;
    }

    public MailUpdateRequest setToSend(Boolean toSend) {
        if (this.toSend == false){
            this.toSend = toSend;
        }
        return this;
    }
    public Boolean getRead() {
        return read;
    }

    public String getBox() {
        return box;
    }
    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
    public Boolean getToSend() {
        return toSend;
    }
}