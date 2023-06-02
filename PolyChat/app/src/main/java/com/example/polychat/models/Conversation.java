package com.example.polychat.models;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.polychat.controllers.db.room.Converters;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "POLY_CONVERSATIONS")
public class Conversation implements Serializable {
    //private  final String CLASSNAME = "CONVERSATION";
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    @ColumnInfo(name = "conversation_id")
    private String conversationId;
    @ColumnInfo(name = "label")
    private String label;
    @ColumnInfo(name = "unreadCount")
    private Integer unreadCount;
    @ColumnInfo(name = "lstMsg")
    private String lastMsg;
    @ColumnInfo(name = "timeMsg")
    private Long timeMsg;

    @ColumnInfo(name = "user_ids")
    private ArrayList<String> userIds;

    public Conversation() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public Long getTimeMsg() {
        return timeMsg;
    }

    public void setTimeMsg(Long timeMsg) {
        this.timeMsg = timeMsg;
    }

    public ArrayList<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(ArrayList<String> userIds) {
        this.userIds = userIds;
    }

    public JSONObject toJSON(){
        try {
            JSONObject json = new JSONObject();
            json.append("conversation_id", this.id);
            json.append("conversation_label", this.label);
            return json;
        } catch (JSONException e) {
            Log.e("CONVERSATIONS", "Error al generar el json");
            throw new RuntimeException(e);
        }
    }
}
