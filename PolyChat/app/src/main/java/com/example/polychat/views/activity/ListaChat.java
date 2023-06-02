package com.example.polychat.views.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.polychat.R;
import com.example.polychat.controllers.db.dao.ConversationDAO;
import com.example.polychat.controllers.db.dao.UserDAO;
import com.example.polychat.controllers.managers.ManagerFiles;
import com.example.polychat.controllers.managers.ManagerMessages;
import com.example.polychat.controllers.managers.ManagerUsers;
import com.example.polychat.models.Conversation;
import com.example.polychat.models.User;
import com.example.polychat.views.adapters.ConversationAdapter;
import com.example.polychat.views.adapters.UserAdapter;
import com.example.polychat.views.adapters.RecyclerItemsTouchHelper;
import com.example.polychat.views.interfaces.LaunchConversation;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class ListaChat extends AppCompatActivity implements LaunchConversation, RecyclerItemsTouchHelper.ChatItemsTouchHelperListener {
    private RecyclerView rChat, rContactos;
    private ConversationAdapter adaptadorChat;
    private UserAdapter adaptadorContacto;
    private ImageView menuCall, menuConfig, addUser;
    private EditText emailUser;
    private ConversationDAO conversationDAO;
    private UserDAO userDAO;

    @Override
    public void onBackPressed() {
        // Esto evita que se realice la acción de retroceso predeterminada
        // Puedes realizar alguna otra acción aquí si lo deseas
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setStatusBarColor(getColor(R.color.background));
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_lista_chat);
        initUI();
        requestExternalStoragePermission();
        requestNotificationPermission();
    }

    private void requestExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
    }

    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, REQUEST_NOTIFICATION_PERMISSION);
            }
        }
    }

    private void initUI() {
        conversationDAO = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(ConversationDAO.class);
        userDAO = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(UserDAO.class);
        userDAO.checkUser(ManagerUsers.getCurrentUser().getUserId());
        userDAO.setUserDB(ManagerUsers.getCurrentUser().getUserId());
        ManagerUsers.setUserDAO(userDAO);
        ManagerMessages.getIdiomas();
        ManagerUsers.getContactos(new ManagerUsers.onManagerUsersListenerList() {
            @Override
            public void onSuccess(List<User> users) {
                runOnUiThread(()->{
                    adaptadorContacto.setLista(users);
                });
            }
        });

        rChat = findViewById(R.id.ListaChatRC);
        rContactos = findViewById(R.id.rContactos);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        adaptadorChat = new ConversationAdapter(this);
        rChat.setLayoutManager(llm);
        rChat.setAdapter(adaptadorChat);
        menuCall = findViewById(R.id.menuCall);
        menuConfig = findViewById(R.id.menuConfig);
        addUser = findViewById(R.id.addUser);
        emailUser =findViewById(R.id.emailUser);


        llm = new LinearLayoutManager(this);
        adaptadorContacto = new UserAdapter(this);
        llm.setOrientation(RecyclerView.HORIZONTAL);
        rContactos.setLayoutManager(llm);
        rContactos.setAdapter(adaptadorContacto);
        Observer<List<User>> obsUser = new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                adaptadorContacto.setLista(users);
            }
        };
        userDAO.getAllUsers().observe(this, obsUser);

        Observer<List<Conversation>> obsConversation=new Observer<List<Conversation>>() {
            @Override
            public void onChanged(List<Conversation> contactos) {
                adaptadorChat.setConversations(contactos);
            }
        };
        conversationDAO.getAllConversations().observe(this, obsConversation);


        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerItemsTouchHelper(0, ItemTouchHelper.LEFT, ListaChat.this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(rChat);

        menuCall.setOnClickListener(v -> Toast.makeText(this, "Esta funcionalidad se encuentra en desarrollo", Toast.LENGTH_SHORT).show());
        menuConfig.setOnClickListener(v ->{
            Intent intent = new Intent(getApplicationContext(), ActivityConfig.class);
            startActivity(intent);
        });
        addUser.setOnClickListener(v ->{
            ManagerUsers.addFriendUser(emailUser.getText().toString());
            emailUser.setText("");
        });
    }

    @Override
    public void openConversation(Conversation c) {
        Intent intent = new Intent(getApplicationContext(), ConversacionActivity.class);
        intent.putExtra("conversacion", c);
        c.setUnreadCount(0);
        conversationDAO.updateConversation(c);
        startActivity(intent);
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof ConversationAdapter.ConversationHolder) {

            List<Conversation> listaChat = adaptadorChat.getLista();
            Conversation chat = listaChat.get(viewHolder.getAdapterPosition());
            String nombre = chat.getLabel();
            Integer indexPosition = viewHolder.getAdapterPosition();

            adaptadorChat.removeItem(viewHolder.getAdapterPosition());
            conversationDAO.deleteConversation(chat.getConversationId());
            Snackbar snackbar = Snackbar.make(((ConversationAdapter.ConversationHolder) viewHolder).layoutDeleteCard, "La conversación con " +nombre + " eliminada", Snackbar.LENGTH_SHORT);
            snackbar.setAction("Deshacer", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adaptadorChat.restoreItem(chat, indexPosition);
                    conversationDAO.addConversation(chat);
                }
            });

            snackbar.setActionTextColor(Color.GREEN);
            snackbar.show();
        }
    }
}