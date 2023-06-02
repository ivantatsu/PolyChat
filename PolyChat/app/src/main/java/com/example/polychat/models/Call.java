package com.example.polychat.models;

import android.util.Log;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

@Entity(tableName = "POLY_CALLS")
public class Call implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    private String callId;
    private String userId;
    private Long duration;
    private Long timeCall;
    private Integer status;

    public Call() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getTimeCall() {
        return timeCall;
    }

    public void setTimeCall(Long timeCall) {
        this.timeCall = timeCall;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public JSONObject toJSON(){
        try {
            JSONObject json = new JSONObject();
            json.append("call_id", this.id);
            json.append("caller_id", this.userId);
            json.append("duration", this.duration);
            json.append("timeCall", this.timeCall);
            json.append("status", this.status);
            return json;
        } catch (JSONException e) {
            Log.e("CALL", "Error al generar el json");
            throw new RuntimeException(e);
        }
    }
}
