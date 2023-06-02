package com.example.polychat.controllers.managers;

import android.util.Log;

import com.amplifyframework.api.ApiCategory;
import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.core.Amplify;
import com.example.polychat.controllers.db.dao.ConversationDAO;
import com.example.polychat.controllers.db.dao.MessageDAO;
import com.example.polychat.models.Conversation;
import com.example.polychat.models.Idioma;
import com.example.polychat.models.Message;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ManagerMessages {
    private static  ApiCategory api = Amplify.API;
    private static final String ENVIROMENT = "PolyChat";
    private static List<Idioma> idiomas = new ArrayList<>();

    public static MessageDAO getMessageDAO() {
        return messageDAO;
    }

    public static void setMessageDAO(MessageDAO messageDAO) {
        ManagerMessages.messageDAO = messageDAO;
    }

    private static MessageDAO messageDAO;

    public static ConversationDAO getConversationDAO() {
        return conversationDAO;
    }

    public static void setConversationDAO(ConversationDAO conversationDAO) {
        ManagerMessages.conversationDAO = conversationDAO;
    }

    private static ConversationDAO conversationDAO;


    public static List<Idioma> getLanguages() {
        return idiomas;
    }

    public void setLanguages(List<Idioma> languages) {
        this.idiomas = languages;
    }

    public static Message getMessage(String id){
        Message message = new Message();
        RestOptions options = RestOptions.builder()
                .addPath("/message/" + id)
                .build();
        api.get(ENVIROMENT, options,
                response -> {
                    byte[] rawBytes = response.getData().getRawBytes();
                    String responseData = new String(rawBytes);
                    try {
                        JSONObject json = new JSONObject(responseData);
                        if(json.getInt("statusCode") == 200){
                            json = json.getJSONObject("body");
                            message.setMessageId(json.getString("message_id"));
                            message.setIdConversation(json.getString("conversation_id"));
                            message.setStatus(json.getInt("status"));
                            message.setMessageType(json.getString("message_type"));
                            message.setContent(json.getString("content"));
                            message.setUser_id(json.getString("sender_id"));
                            message.setTimeSend(json.getLong("timeSend"));
                            message.setPathS3(json.getString("pathS3"));
                        }
                    } catch (JSONException e) {
                        Log.e("Amplify API", "Error al hacer el parse a JSON: " + e.getMessage(), e);
                    }
                },
                error -> {
                    // Maneja el error de la API
                    Log.e("Amplify API", "Error al llamar a la API: " + error.getMessage(), error);
                }
        );
        return message;
    }

    public static Message getTranslation(String message_id, int id, String codIdioma){
        Message message = new Message();
        Log.i("Amplify API", message_id + " - " + id + " - "  +codIdioma);
        RestOptions options = RestOptions.builder()
                .addPath("/message/" + message_id + "/" + codIdioma)
                .build();
        api.get(ENVIROMENT, options,
                response -> {
                    byte[] rawBytes = response.getData().getRawBytes();
                    String responseData = new String(rawBytes);
                    try {
                        JSONObject json = new JSONObject(responseData);
                        Log.i("Amplify API", json.toString());
                        if(json.getInt("statusCode") == 200){
                            String bodyString = json.getString("body");
                            json = new JSONObject(bodyString);
                            message.setId(id);
                            message.setMessageId(json.getString("message_id"));
                            message.setIdConversation(json.getString("conversation_id"));
                            message.setStatus(json.getInt("status"));
                            message.setMessageType(json.getString("message_type"));
                            message.setContent(json.getString("content"));
                            message.setUser_id(json.getString("sender_id"));
                            message.setTimeSend(json.getLong("timeSend"));
                            messageDAO.updateMessage(message);
                        }
                        Log.i("Amplify API",json.toString());
                    } catch (JSONException e) {
                        Log.e("Amplify API", "Error al hacer el parse a JSON: " + e.getMessage(), e);
                    }
                },
                error -> {
                    // Maneja el error de la API
                    Log.e("Amplify API", "Error al llamar a la API: " + error.getMessage(), error);
                }
        );
        return message;
    }

    public static void getIdiomas(){
        Message message = new Message();
        RestOptions options = RestOptions.builder()
                .addPath("/message/idiomas")
                .build();
        api.get(ENVIROMENT, options,
                response -> {
                    byte[] rawBytes = response.getData().getRawBytes();
                    String responseData = new String(rawBytes);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(responseData);
                        if (jsonObject.getInt("statusCode") == 200) {
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("body"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonIdioma = jsonArray.getJSONObject(i);
                                Idioma idioma = new Idioma();
                                idioma.setCodIdioma(jsonIdioma.getString("codIdioma"));
                                idioma.setLabelIdioma(jsonIdioma.getString("labelIdioma"));
                                idiomas.add(idioma);
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("Amplify API", "Error al transformar la respuesta a JSON ");
                    }
                },
                error -> {
                    // Maneja el error de la API
                    Log.e("Amplify API", "Error al llamar a la API: " + error.getMessage(), error);
                }
        );
    }




    public static void deleteMessage(String id){
        RestOptions options = RestOptions.builder()
                .addPath("/message/"+ id)
                .build();
        Amplify.API.delete(options,
            response -> {
                    byte[] rawBytes = response.getData().getRawBytes();
                    String responseData = new String(rawBytes);
                    try {
                        JSONObject json = new JSONObject(responseData);
                        Integer codeStatus = json.getInt("statusCode");
                        switch (codeStatus){
                            case 200:
                                Log.i("Amplify API", "DELETE succeeded: " + response);
                                break;
                            case 400:
                                Log.e("Amplify API", "Invalid request. Missing message_id");
                                break;
                            case 404:
                                Log.e("Amplify API", "Message not found");
                                break;
                        }
                    } catch (JSONException e) {
                        Log.e("Amplify API", "Error al hacer el parse a JSON: " + e.getMessage(), e);
                    }
                },
            error -> {
                    Log.e("Amplify API", "DELETE failed.", error);
                }
        );
    }

    public interface OnMessageAddedListener {
        void onMessageAdded(String messageId);
    }

    public static void addMessage(Message message, Conversation conversation, OnMessageAddedListener listener) {
        JSONObject json = new JSONObject();
        try {
            json.put("message_id", message.getMessageId());
            json.put("sender_id", message.getUser_id());
            json.put("conversation_id", message.getIdConversation());
            json.put("conversation_label", conversation.getLabel());
            json.put("content", message.getContent());
            json.put("message_type", message.getMessageType());
            json.put("timeSend", message.getTimeSend());
            json.put("status", message.getStatus());
            json.put("pathS3", message.getPathS3());
            json.put("userIds", new JSONArray(conversation.getUserIds()));
        } catch (JSONException e) {
            Log.e("JSON", "JSON parse failed.", e);
        }

        ManagerSockets.sendMessage(json);

        RestOptions options = RestOptions.builder()
                .addPath("/message")
                .addBody(json.toString().getBytes())
                .build();

        Amplify.API.post(options,
                response -> {
                    try {
                        byte[] rawBytes = response.getData().getRawBytes();
                        String responseData = new String(rawBytes);
                        JSONObject jsonResponse = new JSONObject(responseData);
                        if (jsonResponse.getInt("statusCode") == 200) {
                            String data = jsonResponse.getString("body");
                            jsonResponse = new JSONObject(data);
                            String messageId = jsonResponse.getString("message_id");
                            listener.onMessageAdded(messageId); // Llamada a la función de devolución de llamada
                            Log.i("Amplify API", "POST succeeded: " + messageId);
                        } else {
                            listener.onMessageAdded(""); // Llamada a la función de devolución de llamada con valor vacío
                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    Log.e("Amplify API", "POST failed.", error);
                    listener.onMessageAdded(""); // Llamada a la función de devolución de llamada con valor vacío en caso de error
                }
        );
    }

    public static void updateMessage(Message message, Conversation conversation){
        JSONObject json = new JSONObject();
        try {
            json.append("message_id", message.getMessageId());
            json.append("sender_id", message.getUser_id());
            json.append("conversation_id",message.getIdConversation());
            json.append("conversation_label", conversation.getLabel());
            json.append("content", message.getContent());
            json.append("message_type", message.getMessageType());
            json.append("timeSend", message.getTimeSend());
            json.append("status", message.getStatus());
            json.append("pathS3", message.getPathS3());
        } catch (JSONException e) {
            Log.e("JSON", "JSON parse failed.", e);
        }

        RestOptions options = RestOptions.builder()
                .addPath("/message")
                .addBody(json.toString().getBytes())
                .build();

        Amplify.API.put(options,
                response -> {
                    Log.e("Amplify API", response.toString());
                    byte[] rawBytes = response.getData().getRawBytes();
                    String responseData = new String(rawBytes);
                    Log.i("Amplify API", "POST succeeded: " + responseData);

                    },
                error -> Log.e("Amplify API", "POST failed.", error)
        );
    }
}
