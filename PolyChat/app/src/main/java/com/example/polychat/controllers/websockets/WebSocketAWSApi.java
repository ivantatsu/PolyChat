package com.example.polychat.controllers.websockets;
import android.util.Log;

import com.example.polychat.controllers.managers.ManagerMessages;
import com.example.polychat.controllers.managers.ManagerUsers;
import com.example.polychat.models.Conversation;
import com.example.polychat.models.Message;
import com.example.polychat.models.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


public class WebSocketAWSApi {
    private WebSocket webSocket;

    public WebSocketAWSApi(){}

    public final class EchoWebSocketListener extends WebSocketListener {

        private String userId;
        public EchoWebSocketListener(String userId) {
            this.userId = userId;
        }

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            WebSocketAWSApi.this.webSocket = webSocket;
            if (this.userId == null) {
                Log.e("WEBSOCKET-API", "UserId is null");
            } else {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("action", "connect");
                    JSONObject dataObject = new JSONObject();
                    dataObject.put("user_id", this.userId);
                    jsonObject.put("data", dataObject);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                webSocket.send(jsonObject.toString());
                Log.i("WEBSOCKET-API","UserId: "+ this.userId);
            }
        }
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            try {
                JSONObject jsonMessage = new JSONObject(text);
                if (jsonMessage.has("action")) {
                    String action = jsonMessage.getString("action");
                    if (action.equals("updateContacts")) {
                        Log.i("WEBSOCKET-API", "UpdateContacts");
                        ManagerUsers.getContactos(new ManagerUsers.onManagerUsersListenerList() {
                            @Override
                            public void onSuccess(List<User> users) {
                                Log.i("WEBSOCKETS", "CONTACTOS ACTUALIZADOS");
                            }
                        });
                    } else if (action.equals("sendMessage")) {
                        Log.i("WEBSOCKET-API", "SendMessage");
                        Message message = new Message();
                        Conversation conversation = new Conversation();
                        jsonMessage = jsonMessage.getJSONObject("message");
                        message.setMessageId(jsonMessage.getString("message_id"));
                        message.setUser_id(jsonMessage.getString("sender_id"));
                        message.setIdConversation(jsonMessage.getString("conversation_id"));
                        conversation.setConversationId(jsonMessage.getString("conversation_id"));
                        conversation.setLabel(jsonMessage.getString("conversation_label"));
                        message.setContent(jsonMessage.getString("content"));
                        conversation.setLastMsg(jsonMessage.getString("content"));
                        message.setMessageType(jsonMessage.getString("message_type"));
                        message.setTimeSend(jsonMessage.getLong("timeSend"));
                        conversation.setTimeMsg(jsonMessage.getLong("timeSend"));
                        message.setStatus(jsonMessage.getInt("status"));
                        message.setPathS3(jsonMessage.getString("pathS3"));

                        Gson gson = new Gson();
                        Type userListType = new TypeToken<List<String>>(){}.getType();
                        String usersString = gson.fromJson(String.valueOf(jsonMessage), JsonObject.class).get("userIds").getAsString();
                        ArrayList<String> users = gson.fromJson(usersString, userListType);
                        conversation.setUserIds(users);
                        ManagerMessages.getMessageDAO().addMessage(message);
                        ManagerMessages.getConversationDAO().updateConversation(conversation);
                    }
                    Log.i("WEBSOCKET-API", text);
                }
            } catch (JSONException e) {
                Log.e("WEBSOCKET-API", "Error parsing JSON", e);
            }
        }
        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            Log.i("WEBSOCKET-API",bytes.hex());
        }
        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(1000, null);
            Log.i("WEBSOCKET-API","Closing : " + code + " / " + reason);
        }
        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            Log.e("WEBSOCKET-API","Error : " + t.getMessage());
        }
    }

    public void start(String connection, String user_id) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(connection)
                .build();
        EchoWebSocketListener listener = new EchoWebSocketListener(user_id);
        client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
    }

    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, "Socket closed by client");
            webSocket = null; // Limpia la referencia al WebSocket para liberar recursos
        }
    }

    // Este método puede ser llamado para enviar un mensaje.
    public void sendMessage(String message) {
        if (webSocket != null) {
            webSocket.send(message);
        }
    }
}
