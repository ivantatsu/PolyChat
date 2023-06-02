package com.example.polychat.views.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.auth.AuthUser;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.polychat.R;
import com.example.polychat.controllers.managers.ManagerFiles;
import com.example.polychat.controllers.managers.ManagerUsers;
import com.example.polychat.models.User;

public class ActivityConfig extends AppCompatActivity {

    private ImageView imgProfile, menuChat, menuCall;
    private TextView btnEdit, btnNotif, btnInfo, btnSupport, btnLogOut, nameText, iniciales;
    private ConstraintLayout status;
    private AuthUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setStatusBarColor(getColor(R.color.background));
        setContentView(R.layout.activity_config);
        initUI();
    }

    private void initUI(){
        currentUser = ManagerUsers.getCurrentUser();
        menuCall = findViewById(R.id.menuCall);
        menuChat = findViewById(R.id.menuChat);
        btnEdit = findViewById(R.id.emailUser);
        btnNotif = findViewById(R.id.btnNotificaciones);
        btnInfo = findViewById(R.id.btnSeguridad);
        btnSupport = findViewById(R.id.btnSoporte);
        btnLogOut = findViewById(R.id.btnLogOut);
        imgProfile = findViewById(R.id.imagenPerfil);
        iniciales = findViewById(R.id.iniciales);
        nameText = findViewById(R.id.titleUser);
        status = findViewById(R.id.statusConfig);
        User user = ManagerUsers.getUserDAO().getUser();
        int colorRes;
        int color;
        switch (user.getStatus()) {
            case 0:
                colorRes = R.color.connectedStatus;
                color = ContextCompat.getColor(getApplicationContext(), colorRes);
                status.setBackgroundTintList(ColorStateList.valueOf(color));
                break;
            case 1:
                colorRes = R.color.afkStatus;
                color = ContextCompat.getColor(getApplicationContext(), colorRes);
                status.setBackgroundTintList(ColorStateList.valueOf(color));
                break;
            case 2:
                colorRes = R.color.desconectedStatus;
                color = ContextCompat.getColor(getApplicationContext(), colorRes);
                status.setBackgroundTintList(ColorStateList.valueOf(color));
                break;
        }
        nameText.setText(user.getUserName());
        String[] nombre = user.getUserName().split(" ");
        String letters = "";
        for (String n : nombre) {
            if(nombre.length == 1){
                letters += n.substring(0, 2);
            }else{
                if (iniciales.length()<3){
                    letters += n.substring(0, 1);
                }
            }
        }
        iniciales.setText(letters.toUpperCase());
        ManagerUsers.getUser(ManagerUsers.getCurrentUser().getUserId(), new ManagerUsers.onManagerUsersListenerUser() {
            @Override
            public void onSuccess(User user) {
                ManagerFiles.existFileInS3(user.getUserId(), new ManagerFiles.OnFileExistListener() {
                    @Override
                    public void onFileExists(Boolean exist, String url) {
                        if(exist){
                            runOnUiThread(() ->{
                                Glide.with(getApplicationContext())
                                        .load(url)
                                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                        .into(imgProfile);
                            });
                        }else{
                            runOnUiThread(() -> {
                                imgProfile.setVisibility(View.GONE);
                                iniciales.setVisibility(View.VISIBLE);
                            });
                        }
                    }
                });
            }
        });

        menuCall.setOnClickListener(v -> Toast.makeText(this, "Esta funcionalidad se encuentra en desarrollo", Toast.LENGTH_SHORT).show());
        menuChat.setOnClickListener(v ->{
            Intent intent = new Intent(getApplicationContext(), ListaChat.class);
            startActivity(intent);
        });

        btnLogOut.setOnClickListener(v->{
            ManagerUsers.logOut(new ManagerUsers.onManagerUsersListenerVoid() {
                @Override
                public void onSuccess() {
                    Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                    startActivity(intent);
                }
                @Override
                public void onError() {
                }
            });
        });
        btnNotif.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), NotificationsActivity.class);
            startActivity(intent);
        });
        btnEdit.setOnClickListener(v ->{
            Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
            intent.putExtra("user", ManagerUsers.getUserDAO().getUser());
            startActivity(intent);
        });

    }
}