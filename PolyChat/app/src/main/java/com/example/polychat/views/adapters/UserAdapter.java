package com.example.polychat.views.adapters;


import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.polychat.R;
import com.example.polychat.controllers.managers.ManagerUsers;
import com.example.polychat.models.Conversation;
import com.example.polychat.models.User;
import com.example.polychat.views.interfaces.LaunchConversation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ContactoHolder> {
    private List<User> userList = new ArrayList<>();
    private Context context;
    private LaunchConversation interfaceConversation;

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        interfaceConversation = (LaunchConversation) context;
    }

    public UserAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ContactoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater v = LayoutInflater.from(parent.getContext()); //creo el LayoutInflater
        ContactoHolder m = new ContactoHolder(v.inflate(R.layout.card_contacto, parent, false));
        return m;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactoHolder holder, int position) {
        holder.imprimir(position);
    }

    public void setLista(List<User> lista) {
        userList.clear();
        userList.addAll(lista);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ContactoHolder extends RecyclerView.ViewHolder {
        private TextView inicialesContacto, nombreContacto;
        private ConstraintLayout status;

        public ContactoHolder(@NonNull View itemView) {
            super(itemView);
            this.inicialesContacto = itemView.findViewById(R.id.inicialesContacto);
            this.nombreContacto = itemView.findViewById(R.id.nombreContacto);
            this.status = itemView.findViewById(R.id.status);
            status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getLayoutPosition() != 0){
                        Conversation c = new Conversation();
                        UUID uuid = UUID.randomUUID();
                        User user = userList.get(getLayoutPosition());
                        c.setConversationId(uuid.toString());
                        c.setLabel(user.getUserName());
                        ArrayList<String> list = new ArrayList<>();
                        list.add(user.getUserId());
                        list.add(ManagerUsers.getCurrentUser().getUserId());
                        c.setUserIds(list);
                        interfaceConversation.openConversation(c);
                        notifyItemChanged(getLayoutPosition());
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialogTheme);
                        builder.setTitle("Cambiar estado");

                        builder.setItems(new CharSequence[]{"Conectado", "Ausente","Desconectado"}, (dialog, which) -> {
                            changeStatus(which);
                            ManagerUsers.changeStatus(which);
                            ManagerUsers.getUserDAO().changeStatus(ManagerUsers.getCurrentUser().getUserId(),which);

                        });
                        builder.show();
                    }
                }
            });
        }

        public void imprimir(int position) {
            User user= userList.get(position);
            String[] nombre = user.getUserName().split(" ");
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
            changeStatus(user.getStatus());
            inicialesContacto.setText(iniciales);
            nombreContacto.setText(nombre[0]);
        }

        public void changeStatus(Integer which){
            int colorRes;
            int color;
            switch (which){
                case 0:
                    colorRes = R.color.connectedStatus;
                    color = ContextCompat.getColor(context, colorRes);
                    status.setBackgroundTintList(ColorStateList.valueOf(color));
                    break;
                case 1:
                    colorRes = R.color.afkStatus;
                    color = ContextCompat.getColor(context, colorRes);
                    status.setBackgroundTintList(ColorStateList.valueOf(color));
                    break;
                case 2:
                    colorRes = R.color.desconectedStatus;
                    color = ContextCompat.getColor(context, colorRes);
                    status.setBackgroundTintList(ColorStateList.valueOf(color));
                    break;
            }
        }
    }


}

