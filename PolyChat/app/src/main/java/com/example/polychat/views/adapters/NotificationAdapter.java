package com.example.polychat.views.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.polychat.R;
import com.example.polychat.controllers.db.dao.UserDAO;
import com.example.polychat.controllers.managers.ManagerUsers;
import com.example.polychat.models.Conversation;
import com.example.polychat.models.User;
import com.example.polychat.views.interfaces.LaunchConversation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.RequestHolder> {
    private List<User> notifiationList = new ArrayList<>();
    private Context context;
    private UserDAO userDAO;

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public NotificationAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<User> users){
        this.notifiationList.clear();
        this.notifiationList.addAll(users);
        notifyDataSetChanged();
    }
    public void setUserDao(UserDAO userDAO){
        this.userDAO = userDAO;
    }
    @NonNull
    @Override
    public RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater v = LayoutInflater.from(parent.getContext());
        RequestHolder m = new RequestHolder(v.inflate(R.layout.card_notificacion, parent, false));
        return m;
    }

    @Override
    public void onBindViewHolder(@NonNull RequestHolder holder, int position) {
        holder.imprimir(position);
    }

    @Override
    public int getItemCount() {
        return notifiationList.size();
    }

    public class RequestHolder extends RecyclerView.ViewHolder {
        private TextView inicialesContacto, nombreContacto;
        private ImageView declineImg, acceptImg;
        public RequestHolder(@NonNull View itemView) {
            super(itemView);
            this.inicialesContacto = itemView.findViewById(R.id.inicialesUser);
            this.nombreContacto = itemView.findViewById(R.id.nameUser);
            this.declineImg = itemView.findViewById(R.id.declineRequest);
            this.acceptImg = itemView.findViewById(R.id.acceptRequest);

            declineImg.setOnClickListener(v -> {
                ManagerUsers.actionRequestFriend(notifiationList.get(getLayoutPosition()).getUserId(), false);
                notifiationList.remove(getLayoutPosition());
                notifyItemRemoved(getLayoutPosition());
            });

            acceptImg.setOnClickListener(v -> {
                ManagerUsers.actionRequestFriend(notifiationList.get(getLayoutPosition()).getUserId(), true);
                notifiationList.remove(getLayoutPosition());
                notifyItemRemoved(getLayoutPosition());
            });
        }

        public void imprimir(int position) {
            User user= notifiationList.get(position);
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

            inicialesContacto.setText(iniciales);
            nombreContacto.setText(nombre[0]);
        }
    }


}

