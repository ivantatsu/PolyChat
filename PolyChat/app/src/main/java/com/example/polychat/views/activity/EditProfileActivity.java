package com.example.polychat.views.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.core.Amplify;
import com.example.polychat.R;
import com.example.polychat.controllers.managers.ManagerUsers;
import com.example.polychat.models.User;

import java.util.ArrayList;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {

    private EditText emailField, userNameField, oldPasswordField, newPasswordField;
    private ImageView btnBack;
    private TextView iniciales,userName;
    private Button update;
    private ConstraintLayout status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setStatusBarColor(getColor(R.color.background));
        setContentView(R.layout.activity_edit_profile);
        initUI();
    }

    public void initUI(){
        Intent intent = getIntent();
        User user = intent.getSerializableExtra("user", User.class);
        emailField = findViewById(R.id.emailField2);
        userNameField = findViewById(R.id.userField2);
        oldPasswordField = findViewById(R.id.oldPasswordField);
        newPasswordField = findViewById(R.id.newPasswordField);
        update = findViewById(R.id.editProfile);
        status = findViewById(R.id.statusConfig);
        iniciales = findViewById(R.id.iniciales);
        userName = findViewById(R.id.titleUser);
        btnBack = findViewById(R.id.btnBack3);
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
        userName.setText(user.getUserName());
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
        update.setOnClickListener(v -> {
            actualizarUsuario(emailField.getText().toString(), userNameField.getText().toString(),
                    oldPasswordField.getText().toString(), newPasswordField.getText().toString());
            Intent newintent = new Intent(getApplicationContext(), ActivityConfig.class);
            startActivity(newintent);
        });
        btnBack.setOnClickListener(v ->{
            Intent newintent2 = new Intent(getApplicationContext(), ActivityConfig.class);
            startActivity(newintent2);
        });
    }

    public void actualizarUsuario(String nuevoEmail, String nuevoNombreUsuario, String nuevaContraseña, String oldPassword) {
        List<AuthUserAttribute> attributesToUpdate = new ArrayList<>();

        if (nuevoEmail != null && !nuevoEmail.isEmpty()) {
            AuthUserAttribute userEmail = new AuthUserAttribute(AuthUserAttributeKey.email(), nuevoEmail);
            attributesToUpdate.add(userEmail);
        }

        if (nuevoNombreUsuario != null && !nuevoNombreUsuario.isEmpty()) {
            AuthUserAttribute usernameAttribute = new AuthUserAttribute(AuthUserAttributeKey.nickname(), nuevoNombreUsuario);
            attributesToUpdate.add(usernameAttribute);
        }

        if (nuevaContraseña != null && !nuevaContraseña.isEmpty()) {
            Amplify.Auth.updatePassword(
                    oldPassword,
                    nuevaContraseña,
                    () -> Log.i("AuthQuickstart", "Updated password successfully"),
                    error -> Toast.makeText(this,error.toString(),Toast.LENGTH_SHORT).show()
            );

        if (!attributesToUpdate.isEmpty()) {
            Amplify.Auth.updateUserAttributes(attributesToUpdate,
                    result -> {
                        Log.i("AuthDemo", "Updated user attribute = " + result.toString());
                        ManagerUsers.updateUser(nuevoEmail, nuevoNombreUsuario);
                    },
                    error -> Log.e("AuthDemo", "Failed to update user attribute.", error)
                );
            }
        }
    }

}