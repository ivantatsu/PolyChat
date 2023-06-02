package com.example.polychat.controllers.managers;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.amplifyframework.api.ApiCategory;
import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthUser;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.auth.result.AuthSignOutResult;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.Consumer;
import com.example.polychat.controllers.db.dao.UserDAO;
import com.example.polychat.models.Conversation;
import com.example.polychat.models.Message;
import com.example.polychat.models.User;
import com.example.polychat.views.activity.ListaChat;
import com.example.polychat.views.interfaces.OnSessionCheckedListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import kotlin.jvm.internal.TypeReference;
import okhttp3.WebSocket;

public class ManagerUsers {

    private static final String AMPLIFYAUTH = "Amplify Auth";
    private static ApiCategory api = Amplify.API;
    private static final String ENVIROMENT = "PolyChat";
    private static AuthUser currentUser;

    private static UserDAO userDAO;

    public static UserDAO getUserDAO() {
        return userDAO;
    }

    public static void setUserDAO(UserDAO userDAO) {
        ManagerUsers.userDAO = userDAO;
    }
    public static String getTokenNotify() {
        return tokenNotify;
    }

    public static void setTokenNotify(String tokenNotify) {
        ManagerUsers.tokenNotify = tokenNotify;
    }

    private static String tokenNotify;


    public static AuthUser getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(AuthUser currentUser) {
        ManagerUsers.currentUser = currentUser;
    }

    public interface onManagerUsersListener {
        void onSuccess(boolean status);
    }

    public interface onManagerUsersListenerList {
        void onSuccess(List<User> users);
    }

    public interface onManagerUsersListenerGet {
        void onSuccess(String userId);
    }

    public interface onManagerUsersListenerUser {
        void onSuccess(User user);
    }
    public interface onManagerUsersListenerVoid {
        void onSuccess();
        void onError();
    }

    public static void checkSession(OnSessionCheckedListener listener){
        Amplify.Auth.fetchAuthSession(
                result -> {
                    Log.i(AMPLIFYAUTH, result.toString());
                    Boolean isSignedIn = result.isSignedIn();
                    listener.onSessionChecked(isSignedIn);
                },
                error -> {
                    Log.e(AMPLIFYAUTH, error.toString());
                    listener.onSessionChecked(false);
                }
        );
    }

    public static void currentUserSession(ManagerUsers.onManagerUsersListenerGet listener){
        Amplify.Auth.getCurrentUser(
                // onSuccess - se ejecuta cuando se obtiene el usuario actual correctamente
                new Consumer<AuthUser>() {
                    @Override
                    public void accept(AuthUser authUser) {
                        currentUser = authUser;
                        Log.i(AMPLIFYAUTH, authUser.toString());
                        listener.onSuccess(currentUser.getUserId());
                        addToken(currentUser.getUserId());
                    }
                },
                // onError - se ejecuta si hay un error al obtener el usuario actual
                new Consumer<AuthException>() {
                    @Override
                    public void accept(AuthException authException) {
                        Log.e(AMPLIFYAUTH, "MessageAdapater");
                    }
                }
        );
    }

    public static void logIn(String email, String password,ManagerUsers.onManagerUsersListener listener){
        Amplify.Auth.signIn(
                email,
                password,
                result -> {
                    Log.i(AMPLIFYAUTH, result.isSignedIn() ? "Sign in succeeded" : "Sign in not complete");
                    ManagerUsers.getCurrentUser();
                    listener.onSuccess(true);
                },
                error -> Log.e(AMPLIFYAUTH, error.toString())
        );
    }

    public static void registerUser(String email, String userName, String password, ManagerUsers.onManagerUsersListenerVoid listener){
        AuthSignUpOptions options = AuthSignUpOptions.builder()
                .userAttribute(AuthUserAttributeKey.name(), userName)
                .build();
        Amplify.Auth.signUp(email, password, options,
                result -> {
                    listener.onSuccess();
                    Log.i(AMPLIFYAUTH, "Result: Successfully");
                },
                error -> {
                    listener.onError();
                    Log.e(AMPLIFYAUTH, "Error to register new User", error);
                }
        );
    }

    public static void validateReister(String email, String codeValidation, ManagerUsers.onManagerUsersListenerVoid listener){
        Amplify.Auth.confirmSignUp(email,codeValidation,
                resultado -> {
                    listener.onSuccess();
                    Log.i(AMPLIFYAUTH,"Validation result: Successfully");
                },
                errors -> {
                    listener.onError();
                    Log.e(AMPLIFYAUTH, "Error to validate new User", errors);
                }
        );
    }

    public static void addUser(User user, ManagerUsers.onManagerUsersListenerVoid listener) {
        JSONObject json = new JSONObject();
        try {
            json.put("user_id", user.getUserId());
            json.put("userName", user.getUserName());
            json.put("email", user.getEmail());
            json.put("status",user.getStatus());
            json.put("token",user.getToken());
            Log.i("Amplify JSON", json.toString());
        } catch (JSONException e) {
            Log.e("JSON", "JSON parse failed.", e);
        }

        RestOptions options = RestOptions.builder()
                .addPath("/user")
                .addBody(json.toString().getBytes())
                .build();

        Amplify.API.post(options,
                response -> {
                    Log.i("Amplify API", "POST succeeded: ");
                    listener.onSuccess();
                },
                error -> {
                    Log.e("Amplify API", "POST failed.", error);
                }
        );
    }

    public static void getUser(String id, ManagerUsers.onManagerUsersListenerUser listenerUser){
        User user = new User();
        user.setUserId(id);
        RestOptions options = RestOptions.builder()
                .addPath("/user/" + id)
                .build();
        api.get(ENVIROMENT, options,
                response -> {
                    byte[] rawBytes = response.getData().getRawBytes();
                    String responseData = new String(rawBytes);
                    try {
                        JSONObject json = new JSONObject(responseData);
                        if(json.getInt("statusCode") == 200){
                            String body = json.getString("body");
                            Log.i("API",body);
                            JSONObject bodyJson = new JSONObject(body);
                            user.setUserName(bodyJson.getString("userName"));
                            user.setEmail(bodyJson.getString("email"));
                            user.setStatus(bodyJson.getInt("status"));
                            user.setToken(ManagerUsers.getTokenNotify());
                            listenerUser.onSuccess(user);
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
    }

    public static void logOut(ManagerUsers.onManagerUsersListenerVoid listener) {
        Amplify.Auth.signOut(new Consumer<AuthSignOutResult>() {
            @Override
            public void accept(@NonNull AuthSignOutResult value) {
                listener.onSuccess();
            }
        });
    }

    public static void addFriendUser(String email){
        JSONObject json = new JSONObject();
        try {
            json.put("user_id", currentUser.getUserId());
            json.put("email", email);
        } catch (JSONException e) {
            Log.e("JSON", "JSON parse failed.", e);
        }
        Log.i("Amplify USER", json.toString());
        RestOptions options = RestOptions.builder()
                .addPath("/user/addfriend")
                .addBody(json.toString().getBytes())
                .build();

        Amplify.API.post(options,
                response -> {

                    Log.i("Amplify API", "POST succeeded: Add Friend ");
                },
                error -> {
                    Log.e("Amplify API", "POST failed.", error);
                }
        );
    }

    private static void addToken(String userId) {
        JSONObject json = new JSONObject();
        try {
            json.put("user_id", userId);
            json.put("token", ManagerUsers.getTokenNotify());
        } catch (JSONException e) {
            Log.e("JSON", "JSON parse failed.", e);
        }
        Log.i("Amplify JSON", json.toString());
        RestOptions options = RestOptions.builder()
                .addPath("/user/addtoken")
                .addBody(json.toString().getBytes())
                .build();

        Amplify.API.put(options,
                response -> {
                    Log.i("Amplify API", "PUT succeeded: Add Token - " + response.toString()  );
                },
                error -> {
                    Log.e("Amplify API", "PUT failed.", error);
                }
        );
    }

    public static void getNotificationsRequest(ManagerUsers.onManagerUsersListenerList listener){
        RestOptions options = RestOptions.builder()
                .addPath("/user/addfriend/" + currentUser.getUserId())
                .build();
        api.get(ENVIROMENT, options,
                response -> {
                    byte[] rawBytes = response.getData().getRawBytes();
                    String responseData = new String(rawBytes);
                    try {
                        JSONObject json = new JSONObject(responseData);
                        if(json.getInt("statusCode") == 200){
                            // Crea el Gson
                            Gson gson = new Gson();

                            // Extrae la cadena "users" y convierte su valor en una lista de User
                            Type userListType = new TypeToken<List<User>>(){}.getType();

                            // Extrae la cadena de "users"
                            String usersString = gson.fromJson(String.valueOf(json), JsonObject.class).get("users").getAsString();

                            // Analiza la cadena de "users" en una lista de usuarios
                            List<User> users = gson.fromJson(usersString, userListType);
                            listener.onSuccess(users);
                        }
                    } catch (JSONException e) {
                        Log.e("Amplify API", "Error al hacer el parse a JSON: " + e.getMessage(), e);
                    }
                },
                error -> {
                    Log.e("Amplify API", "Error al llamar a la API: " + error.getMessage(), error);
                }
        );
    }

    public static void getContactos(ManagerUsers.onManagerUsersListenerList listener){
        RestOptions options = RestOptions.builder()
                .addPath("/user/contacts/" + currentUser.getUserId())
                .build();
        api.get(ENVIROMENT, options,
                response -> {
                    byte[] rawBytes = response.getData().getRawBytes();
                    String responseData = new String(rawBytes);
                    try {
                        JSONObject json = new JSONObject(responseData);
                        Log.i(AMPLIFYAUTH, json.toString());
                        if(json.getInt("statusCode") == 200){
                            // Crea el Gson
                            Gson gson = new Gson();

                            // Extrae la cadena "users" y convierte su valor en una lista de User
                            Type userListType = new TypeToken<List<User>>(){}.getType();

                            // Extrae la cadena de "users"
                            String usersString = gson.fromJson(String.valueOf(json), JsonObject.class).get("users").getAsString();

                            // Analiza la cadena de "users" en una lista de usuarios
                            List<User> users = gson.fromJson(usersString, userListType);
                            userDAO.deleteUsers(currentUser.getUserId());
                            userDAO.setAllUsers(users);
                            listener.onSuccess(users);
                        }
                    } catch (JSONException e) {
                        Log.e("Amplify API", "Error al hacer el parse a JSON: " + e.getMessage(), e);
                    }
                },
                error -> {
                    Log.e("Amplify API", "Error al llamar a la API: " + error.getMessage(), error);
                }
        );
    }

    public static void actionRequestFriend(String userId, Boolean action){
        JSONObject json = new JSONObject();
        try {
            json.put("user_id", currentUser.getUserId());
            json.put("related_user_id", userId);
            json.put("add_to_contacts", action);
        } catch (JSONException e) {
            Log.e("JSON", "JSON parse failed.", e);
        }
        Log.i("Amplify JSON", json.toString());
        RestOptions options = RestOptions.builder()
                .addPath("/user/addfriend")
                .addBody(json.toString().getBytes())
                .build();

        Amplify.API.put(options,
                response -> {
                    Log.i("Amplify API", "PUT succeeded: Add Token - " + response.toString()  );
                },
                error -> {
                    Log.e("Amplify API", "PUT failed.", error);
                }
        );
    }

    public static void changeStatus(Integer status){
        JSONObject json = new JSONObject();
        try {
            json.put("user_id", currentUser.getUserId());
            json.put("new_status", status);
        } catch (JSONException e) {
            Log.e("JSON", "JSON parse failed.", e);
        }
        Log.i("Amplify JSON", json.toString());
        RestOptions options = RestOptions.builder()
                .addPath("/user/changestatus")
                .addBody(json.toString().getBytes())
                .build();
        Amplify.API.put(options,
                response -> {
                    ManagerUsers.getUserDAO().setUserDB(currentUser.getUserId());
                    ManagerSockets.updateContacts();
                    Log.i("Amplify API", "PUT succeeded: Add Token - " + response.toString()  );
                },
                error -> {
                    Log.e("Amplify API", "PUT failed.", error);
                }
        );
    }

    public static void updateUser(String email, String username){
        JSONObject json = new JSONObject();
        try {
            json.put("user_id", currentUser.getUserId());
            json.put("email", email);
            json.put("userName", username);
        } catch (JSONException e) {
            Log.e("JSON", "JSON parse failed.", e);
        }
        Log.i("Amplify JSON", json.toString());
        RestOptions options = RestOptions.builder()
                .addPath("/user")
                .addBody(json.toString().getBytes())
                .build();

        Amplify.API.put(options,
                response -> {
                    userDAO.getUser().setUserName(username);
                    userDAO.getUser().setEmail(email);
                    userDAO.updateUser(userDAO.getUser());
                    Log.i("Amplify API", "PUT succeeded: Edit Profile- " + response.toString()  );
                },
                error -> {
                    Log.e("Amplify API", "PUT failed.", error);
                }
        );
    }
}
