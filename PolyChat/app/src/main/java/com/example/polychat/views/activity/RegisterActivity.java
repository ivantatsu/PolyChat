package com.example.polychat.views.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.polychat.R;
import com.example.polychat.controllers.db.dao.UserDAO;
import com.example.polychat.controllers.managers.ManagerSockets;
import com.example.polychat.controllers.managers.ManagerUsers;
import com.example.polychat.models.User;
import com.example.polychat.utils.Validaciones;

public class RegisterActivity extends AppCompatActivity {
    private Button btnNewUser, btnVolver;
    private EditText userField, emailField, passField, confirmPassField, codeField;
    private TextView errors;
    private Dialog dialog;
    private UserDAO userDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setStatusBarColor(getColor(R.color.background));
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_register);
        initUI();
    }

    private void initUI() {
        userDAO = new ViewModelProvider((ViewModelStoreOwner) this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(UserDAO.class);
        btnNewUser = findViewById(R.id.btnNuevoUsuario);
        btnVolver = findViewById(R.id.btnVolverLogIn);
        userField = findViewById(R.id.userField);
        emailField = findViewById(R.id.emailField);
        passField = findViewById(R.id.emailUser);
        errors = findViewById(R.id.errors);
        confirmPassField = findViewById(R.id.confirmPassField);
        btnNewUser.setOnClickListener(view -> {
            String err = Validaciones.newUser(userField.getText().toString(), emailField.getText().toString(),
                    passField.getText().toString(), confirmPassField.getText().toString());
            if (err.equals("")) {
                ManagerUsers.registerUser(emailField.getText().toString(), userField.getText().toString(),
                passField.getText().toString(), new ManagerUsers.onManagerUsersListenerVoid() {
                    @Override
                    public void onSuccess() {
                        newDialogConfirm();
                    }
                    @Override
                    public void onError() {
                    }
                });
            } else {
                errors.setText(err);
            }
        });
        btnVolver.setOnClickListener(v ->{
            Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
            startActivity(intent);
        });
    }

    private void newDialogConfirm(){
        runOnUiThread(() -> {
            dialog = new Dialog(this);
            dialog.setContentView(R.layout.custom_dialog_verification);
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_background));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false); //Optional
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog
            codeField = dialog.findViewById(R.id.verificationField);
            Button Okay = dialog.findViewById(R.id.btn_okay);
            Button Cancel = dialog.findViewById(R.id.btn_cancel);
            dialog.show();

            Okay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(RegisterActivity.this, "Okay", Toast.LENGTH_SHORT).show();
                    ManagerUsers.validateReister(emailField.getText().toString(), codeField.getText().toString(),
                            new ManagerUsers.onManagerUsersListenerVoid() {
                                @Override
                                public void onSuccess() {
                                    ManagerUsers.logIn(emailField.getText().toString(), passField.getText().toString(), new ManagerUsers.onManagerUsersListener() {
                                        @Override
                                        public void onSuccess(boolean status) {
                                            ManagerUsers.currentUserSession(new ManagerUsers.onManagerUsersListenerGet() {
                                                @Override
                                                public void onSuccess(String userId) {
                                                    User user = new User();
                                                    user.setUserId(ManagerUsers.getCurrentUser().getUserId());
                                                    user.setUserName(userField.getText().toString());
                                                    user.setEmail(emailField.getText().toString());
                                                    user.setToken(ManagerUsers.getTokenNotify());
                                                    user.setStatus(1);
                                                    ManagerUsers.addUser(user, new ManagerUsers.onManagerUsersListenerVoid() {
                                                        @Override
                                                        public void onSuccess() {
                                                            userDAO.addUser(user);
                                                            ManagerSockets.startWebSocket(user.getUserId());
                                                            Intent intent = new Intent(getApplicationContext(), ListaChat.class);
                                                            startActivity(intent);
                                                        }
                                                        @Override
                                                        public void onError() {}
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }
                                @Override
                                public void onError() {}
                            });
                    dialog.dismiss();
                }
            });

            Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegisterActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        });
    }
}