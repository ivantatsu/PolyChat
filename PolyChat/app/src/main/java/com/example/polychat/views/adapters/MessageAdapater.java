package com.example.polychat.views.adapters;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthUser;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.Consumer;
import com.bumptech.glide.Glide;
import com.example.polychat.R;
import com.example.polychat.controllers.managers.ManagerFiles;
import com.example.polychat.controllers.managers.ManagerMessages;
import com.example.polychat.models.Conversation;
import com.example.polychat.models.Idioma;
import com.example.polychat.models.Message;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MessageAdapater extends RecyclerView.Adapter<MessageAdapater.MenssageHolder> {
    private Conversation conversacion;
    private List<Message> listaMensajes = new ArrayList<>();
    private Context context;
    private String authUserid;

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public MessageAdapater(Context context) {
        this.context = context;
        Amplify.Auth.getCurrentUser(
                // onSuccess - se ejecuta cuando se obtiene el usuario actual correctamente
                new Consumer<AuthUser>() {
                    @Override
                    public void accept(AuthUser authUser) {
                        authUserid = authUser.getUserId();
                        // Aquí puedes utilizar la ID del usuario
                    }
                },
                // onError - se ejecuta si hay un error al obtener el usuario actual
                new Consumer<AuthException>() {
                    @Override
                    public void accept(AuthException authException) {
                        Log.e("AWS-AUTH","MessageAdapater");
                    }
                }
        );
    }

    public void setConversacion(Conversation c) {
        this.conversacion = c;
    }

    public List<Message> getLista() {
        return listaMensajes;
    }

    public void setLista(List<Message> lista) {
        listaMensajes.clear();
        listaMensajes.addAll(lista);
        notifyDataSetChanged();
    }

    @Override
    public void onViewRecycled(@NonNull MenssageHolder holder) {
        super.onViewRecycled(holder);
        holder.itemView.clearAnimation(); // Limpia cualquier animación aplicada a la vista
    }

    @NonNull
    @Override
    public MenssageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater v = LayoutInflater.from(parent.getContext());
        MenssageHolder m = new MenssageHolder(v.inflate(R.layout.card_message, parent, false));
        return m;
    }

    @Override
    public void onBindViewHolder(@NonNull MenssageHolder holder, int position) {
        holder.imprimir(position);
    }

    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }

    public class MenssageHolder extends RecyclerView.ViewHolder {
        TextView mensajeChat;
        LinearLayout mensajeComponent, components;
        ImageView imageViewMessage; // Agregar referencia al ImageView
        private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;

        public MenssageHolder(@NonNull View itemView) {
            super(itemView);
            mensajeChat = itemView.findViewById(R.id.idMsg);
            mensajeComponent = itemView.findViewById(R.id.mensajeLayout);
            components = itemView.findViewById(R.id.ComponentMessage);
            imageViewMessage = itemView.findViewById(R.id.imageViewMessage); // Inicializar el ImageView
            imageViewMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("IMAGE","ONCLICK - "+ listaMensajes.get(getLayoutPosition()).getContent());
                    showImageDialog(listaMensajes.get(getLayoutPosition()).getContent());
                }
            });
            mensajeChat.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Seleccione un idioma para traducir el mensaje");
                    List<Idioma> idiomas = ManagerMessages.getLanguages();
                    String[] languages = new String[idiomas.size()];
                    for (int i = 0; i < idiomas.size(); i++) {
                        languages[i] = idiomas.get(i).getLabelIdioma();
                    }

                    builder.setItems(languages, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // languages[which] contiene el idioma seleccionado
                            Toast.makeText(context, "Idioma seleccionado: " + languages[which], Toast.LENGTH_SHORT).show();
                            Message message = listaMensajes.get(getLayoutPosition());
                            ManagerMessages.getTranslation(message.getMessageId(), message.getId(),idiomas.get(which).getCodIdioma());
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
            });
        }

        private void showImageDialog(String imageUrl) {
            Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_image);
            ImageView imageView = dialog.findViewById(R.id.imageViewDialog);
            Uri imageUri = Uri.parse(imageUrl);
            imageView.setImageURI(imageUri); // Cargar la imagen directamente desde la Uri
            dialog.findViewById(R.id.dialogLayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }


        public void imprimir(int position) {
            Message m = listaMensajes.get(position);

            if (m.getUser_id().equals(authUserid)) {
                mensajeChat.setBackgroundTintList(itemView.getResources().getColorStateList(R.color.myMsg,context.getTheme()));
                components.setGravity(Gravity.RIGHT);
                mensajeComponent.setGravity(Gravity.RIGHT);
                mensajeChat.setBackground(context.getResources().getDrawable(R.drawable.msg_style_right, context.getTheme()));
            } else {
                mensajeChat.setBackground(context.getResources().getDrawable(R.drawable.msg_style_left, context.getTheme()));
                mensajeChat.setBackgroundTintList(itemView.getResources().getColorStateList(R.color.otherMsg,context.getTheme()));
                components.setGravity(Gravity.LEFT);
                mensajeComponent.setGravity(Gravity.LEFT);
            }

            if (m.getMessageType() != null && m.getMessageType().equals("text")) {
                mensajeChat.setText(m.getContent());
                imageViewMessage.setVisibility(View.GONE); // Ocultar ImageView en caso de mensaje de texto
                mensajeChat.setVisibility(View.VISIBLE);
            } else if (m.getMessageType() != null && m.getMessageType().equals("img")) {
                Uri uri = Uri.parse(m.getContent());
                String nameFile = "files" + m.getPathS3().substring(7);
                if(!ManagerFiles.checkExistFileGallery(nameFile,context)){
                    ManagerFiles.downloadFile(m.getPathS3(), uri,context, new ManagerFiles.OnFileDownloadListener() {
                        @Override
                        public void onFileDownload(File file) {
                            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            ManagerFiles.saveImageToGallery(bitmap, file.getAbsolutePath(), context);
                        }
                    });
                }
                Glide.with(context)
                        .load(uri)
                        .centerCrop()
                        .into(imageViewMessage);
                imageViewMessage.setVisibility(View.VISIBLE); // Mostrar ImageView en caso de imagen
                mensajeChat.setVisibility(View.GONE);
            }
        }
    }
}

