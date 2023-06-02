package com.example.polychat.views.activity;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.auth.AuthUser;
import com.example.polychat.R;
import com.example.polychat.controllers.db.dao.ConversationDAO;
import com.example.polychat.controllers.db.dao.MessageDAO;
import com.example.polychat.controllers.managers.ManagerFiles;
import com.example.polychat.controllers.managers.ManagerMessages;
import com.example.polychat.controllers.managers.ManagerUsers;
import com.example.polychat.models.Conversation;
import com.example.polychat.models.Message;
import com.example.polychat.views.adapters.MessageAdapater;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ConversacionActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICKER = 2;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int MAX_ATTEMPTS = 3;
    private String currentPhotoPath;
    private Uri currentPhotoUri;
    private RecyclerView rc;
    private EditText inputMessage;
    private MessageAdapater adaptadorConversacion;
    private ImageView btnSend, btnImg, btnBack, btnCall;
    private AuthUser currentUser;
    private TextView conversationLabel, inicialesView;
    private MessageDAO messageDAO;
    private ConversationDAO conversationDAO;
    private Conversation conversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setStatusBarColor(getColor(R.color.background));
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_conversacion);
        currentUser = ManagerUsers.getCurrentUser();
        initUI();
    }

    private void initUI() {
        //Inicializar los componentes
        rc = findViewById(R.id.listadoMensajes);
        inputMessage = findViewById(R.id.inputMessage);
        btnSend = findViewById(R.id.btnSend);
        btnImg = findViewById(R.id.btnImg);
        btnBack = findViewById(R.id.btnBack);
        btnCall = findViewById(R.id.btnCall);
        conversationLabel = findViewById(R.id.conversationLabel);
        inicialesView = findViewById(R.id.inicialesContacto);

        // Insertar Adaptador y Layout al Recycler View
        LinearLayoutManager llm = new LinearLayoutManager(this);
        adaptadorConversacion = new MessageAdapater(ConversacionActivity.this);
        rc.setLayoutManager(llm);

        Intent intent = getIntent();
        conversation = intent.getSerializableExtra("conversacion", Conversation.class);
        adaptadorConversacion.setConversacion(conversation);
        conversationDAO = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(ConversationDAO.class);
        messageDAO = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(MessageDAO.class);
        ManagerMessages.setMessageDAO(messageDAO);
        ManagerMessages.setConversationDAO(conversationDAO);
        messageDAO.setIdConversation(conversation.getConversationId());
        conversationLabel.setText(conversation.getLabel());
        String[] nombre = conversation.getLabel().split(" ");
        String iniciales = "";
        for (String n : nombre) {
            if(nombre.length == 1){
                iniciales += n.substring(0, 2);
            }else{
                if (iniciales.length()<3){
                    iniciales += n.substring(0, 1);
                }
            }
        }
        inicialesView.setText(iniciales);
        Observer<List<Message>> obs = new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                adaptadorConversacion.setLista(messages);
                rc.scrollToPosition(messages.size() - 1);
            }
        };

        messageDAO.getAllMessage().observe(this, obs);
        rc.setAdapter(adaptadorConversacion);
        adaptadorConversacion.notifyDataSetChanged();

        // Insertar la expansión del input de texto para expandirse en función de la cantidad de texto introducido.
        inputMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int control = inputMessage.getLineHeight() + inputMessage.getLineHeight() * 3;
                if (inputMessage.getHeight() < control) {
                    inputMessage.setHeight(inputMessage.getLineHeight() * inputMessage.getLineCount() + inputMessage.getLineHeight());
                } else if (inputMessage.getLineCount() < 4) {
                    inputMessage.setHeight(inputMessage.getLineHeight() * inputMessage.getLineCount() + inputMessage.getLineHeight());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnSend.setOnClickListener(v -> {
            String str = inputMessage.getText().toString();
            if (!str.equals("")) {
                sendMsg("text", str,"");
            }
        });

        btnCall.setOnClickListener(v->{
            Toast.makeText(this, "Esta funcionalidad se encuentra en desarrollo", Toast.LENGTH_SHORT).show();
        });

        btnBack.setOnClickListener(v->{
            Intent listaChatIntent = new Intent(getApplicationContext(), ListaChat.class);
            startActivity(listaChatIntent);
        });

        btnImg.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Adjuntar imagen");
            builder.setItems(new CharSequence[]{"Adjuntar imagen existente", "Tomar una foto"}, (dialog, which) -> {
                if (which == 0) {
                    dispatchImagePickerIntent();
                } else {
                    dispatchTakePictureIntent();
                }
            });
            builder.show();
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (photoFile != null) {
            currentPhotoUri = FileProvider.getUriForFile(this, "com.example.polychat.fileprovider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri);
            takePictureLauncher.launch(takePictureIntent);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            if (currentPhotoUri != null) {
                sendImg(currentPhotoUri);
            }
        }
    });

    public void sendImg(Uri imgUri) {
        String imgName = currentUser.getUserId() + "_" + System.currentTimeMillis();
        ManagerFiles.uploadFile(imgName, imgUri, ConversacionActivity.this, new ManagerFiles.OnFileAddedListener() {
            @Override
            public void onFileAdded(String messageId) {
                sendMsg("img", imgUri.toString(), messageId);
            }
        });
    }


    ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            sendImg(result, false);
        }
    });



    public void sendImg(ActivityResult result, boolean saveImg){
        Uri imgUri = result.getData().getData();
        String imgName = currentUser.getUserId() +"_" + System.currentTimeMillis();
        final Uri finalImgUri = imgUri;
        ManagerFiles.uploadFile(imgName, imgUri, ConversacionActivity.this, new ManagerFiles.OnFileAddedListener() {
            @Override
            public void onFileAdded(String messageId) {
                sendMsg("img", finalImgUri.toString(), messageId);
            }
        });
    }

    private void sendMsg(String type, String contet, String pathS3){
        Message msg = new Message();
        msg.setContent(contet);
        msg.setMessageType(type);
        msg.setIdConversation(conversation.getConversationId());
        msg.setTimeSend(System.currentTimeMillis());
        msg.setStatus(1);
        msg.setUser_id(currentUser.getUserId());
        msg.setPathS3(pathS3);
        inputMessage.setText("");

        ManagerMessages.addMessage(msg, conversation, new ManagerMessages.OnMessageAddedListener() {
            @Override
            public void onMessageAdded(String messageId) {
                // Aquí puedes usar el valor de messageId obtenido de la llamada a la API
                if(!messageId.equals("")){
                    msg.setMessageId(messageId);
                }else{
                    msg.setStatus(2);
                }
                messageDAO.addMessage(msg);
                if(adaptadorConversacion.getLista().size() == 0){
                    conversation.setTimeMsg(msg.getTimeSend());
                    if(msg.getMessageType().equals("text")){
                        conversation.setLastMsg(msg.getContent());
                    }
                    conversationDAO.addConversation(conversation);
                }else{
                    conversation.setTimeMsg(msg.getTimeSend());
                    if(msg.getMessageType().equals("text")){
                        conversation.setLastMsg(msg.getContent());
                    }
                    conversationDAO.updateConversation(conversation);
                }
            }
        });
    }

    private void dispatchImagePickerIntent() {
        Intent imagePickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerIntent.setType("image/*");
        imagePickerLauncher.launch(imagePickerIntent);
    }

}