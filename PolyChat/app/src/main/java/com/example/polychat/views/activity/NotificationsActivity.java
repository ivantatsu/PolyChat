package com.example.polychat.views.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.polychat.R;
import com.example.polychat.controllers.db.dao.UserDAO;
import com.example.polychat.controllers.managers.ManagerUsers;
import com.example.polychat.models.User;
import com.example.polychat.views.adapters.NotificationAdapter;


import java.util.List;

public class NotificationsActivity extends AppCompatActivity {
    private RecyclerView rNotificacion;
    private ImageView btnBack;
    private NotificationAdapter notificationAdapter;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setStatusBarColor(getColor(R.color.background));
        setContentView(R.layout.activity_notification);
        initUI();
    }

    private void initUI() {
        notificationAdapter = new NotificationAdapter(this);
        userDAO = new ViewModelProvider(this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(UserDAO.class);
        rNotificacion = findViewById(R.id.ListaNotificacionesRC);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        notificationAdapter.setUserDao(userDAO);
        rNotificacion.setLayoutManager(llm);
        rNotificacion.setAdapter(notificationAdapter);
        ManagerUsers.getNotificationsRequest(new ManagerUsers.onManagerUsersListenerList() {
            @Override
            public void onSuccess(List<User> users) {
                runOnUiThread(()->{
                    notificationAdapter.setList(users);
                });
            }
        });
        btnBack = findViewById(R.id.btnBack2);
        btnBack.setOnClickListener(v ->{
            Intent intent = new Intent(getApplicationContext(), ActivityConfig.class);
            startActivity(intent);
        });
    }
}