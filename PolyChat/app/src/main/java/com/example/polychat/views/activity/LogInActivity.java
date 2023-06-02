package com.example.polychat.views.activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.polychat.R;
import com.example.polychat.controllers.managers.ManagerSockets;
import com.example.polychat.controllers.managers.ManagerUsers;
import com.example.polychat.views.interfaces.OnSessionCheckedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class LogInActivity extends AppCompatActivity {
    private Button btnLogIn, btnRegister;
    private EditText passField, emailField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setStatusBarColor(getColor(R.color.background));
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                    return;
                }
                String token = task.getResult();
                ManagerUsers.setTokenNotify(token);
                ManagerUsers.checkSession(new OnSessionCheckedListener() {
                    @Override
                    public void onSessionChecked(boolean isSignedIn) {
                        if(isSignedIn){
                            ManagerUsers.currentUserSession(new ManagerUsers.onManagerUsersListenerGet() {
                                @Override
                                public void onSuccess(String userId) {
                                    ManagerSockets.startWebSocket(userId);
                                    Intent intent = new Intent(getApplicationContext(), ListaChat.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                });
            }
        });

        initUI();
    }

    protected void initUI() {

        btnLogIn = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        emailField = findViewById(R.id.emailField);
        passField = findViewById(R.id.emailUser);

        btnLogIn.setOnClickListener(v -> {
            String email = emailField.getText().toString();
            String password = passField.getText().toString();
            if (!email.equals("") && !password.equals("")) {
                ManagerUsers.logIn(email, password, new ManagerUsers.onManagerUsersListener() {
                    @Override
                    public void onSuccess(boolean status) {
                        ManagerUsers.currentUserSession(new ManagerUsers.onManagerUsersListenerGet() {
                            @Override
                            public void onSuccess(String userId) {
                                ManagerSockets.startWebSocket(userId);
                                Intent intent = new Intent(getApplicationContext(), ListaChat.class);
                                startActivity(intent);
                            }
                        });
                    }
                });
            }
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ManagerSockets.closeWebSocket();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ManagerSockets.closeWebSocket();
    }

}