package com.example.polychat.models;

import android.util.Log;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

@Entity(tableName = "POLY_USERS")
public class User implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    @SerializedName("user_id")
    private String userId;
    @SerializedName("userName")
    private String userName;
    @SerializedName("email")
    private String email;
    @SerializedName("status")
    private Integer status;
    @SerializedName("token")
    private String token;

    public User() {
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
            json.append("user_id", this.id);
            json.append("userName", this.userName);
            json.append("email", this.email);
            json.append("status", this.status);
            return json;
        } catch (JSONException e) {
            Log.e("USER", "Error al generar el json");
            throw new RuntimeException(e);
        }
    }
}
