package com.example.polychat.controllers.managers;

import android.util.Log;

import com.example.polychat.controllers.websockets.WebSocketAWSApi;

import org.json.JSONException;
import org.json.JSONObject;

public class ManagerSockets {
    private static final String WEBSOCKETAPI_ENDPOINT = "wss://3tf56qasy8.execute-api.eu-west-3.amazonaws.com/PolyChat";
    private static WebSocketAWSApi webSocketAWSApi;

    public static void startWebSocket(String userId){
        webSocketAWSApi = new WebSocketAWSApi();
        webSocketAWSApi.start(WEBSOCKETAPI_ENDPOINT, userId);
        Log.i("WebSocketApi", "SocketIniciado");
    }

    public static void sendMessage(JSONObject jsonObject){
        if(webSocketAWSApi != null) {
            JSONObject json = new JSONObject();
            try {
                json.put("action", "sendMessage");
                json.put("data", jsonObject);
            } catch (JSONException e) {
                Log.e("ERROR JSON", "Error al parsear el JSON sendMessage");
            }
            webSocketAWSApi.sendMessage(json.toString());
        }
    }

    public static void updateContacts(){
        if(webSocketAWSApi != null){
            JSONObject json = new JSONObject();
            try {
                json.put("action","updateContacts");
                json.put("data", ManagerUsers.getCurrentUser().getUserId());
            } catch (JSONException e) {
                Log.e("ERROR JSON", "Error al parsear el JSON updateContacts");
            }
            webSocketAWSApi.sendMessage(json.toString());
        }
    }

    public static void closeWebSocket(){
        webSocketAWSApi.close();
    }
}
