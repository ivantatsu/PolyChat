package com.example.polychat.models;

import android.util.Log;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

@Entity(tableName = "POLY_MESSAGES")
public class Message implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    private String messageId;
    private String idConversation;
    private Integer status;
    private Long timeSend;
    private String messageType;
    private String content;

    private String pathS3;
    private String user_id;

    public Message() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getTimeSend() {
        return timeSend;
    }

    public void setTimeSend(Long timeSend) {
        this.timeSend = timeSend;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getIdConversation() {
        return idConversation;
    }

    public String getPathS3() {
        return pathS3;
    }

    public void setPathS3(String pathS3) {
        this.pathS3 = pathS3;
    }

    public void setIdConversation(String idConversation) {
        this.idConversation = idConversation;
    }

    public JSONObject toJSON(){
        try {
            JSONObject json = new JSONObject();
            json.append("message_id", this.id);
            json.append("status", this.status);
            json.append("timeSend", this.timeSend);
            json.append("message_type", this.status);
            json.append("content", this.content);
            json.append("user_id", this.user_id);
            return json;
        } catch (JSONException e) {
            Log.e("MESSAGE", "Error al generar el json");
            throw new RuntimeException(e);
        }
    }
}
