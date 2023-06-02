package com.example.polychat.views.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.polychat.R;
import com.example.polychat.models.Conversation;
import com.example.polychat.views.interfaces.LaunchConversation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationHolder> {
    private ArrayList<Conversation> converationsList = new ArrayList<>();
    private Context context;
    private LaunchConversation interfaceConversation;

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        interfaceConversation = (LaunchConversation) context;
    }

    public ConversationAdapter(Context context) {
        this.context = context;
    }

    public void removeItem(int position){
        converationsList.remove(position);
        notifyItemRemoved(position);
    }

    public void setConversations(List<Conversation> lista){
        converationsList = (ArrayList<Conversation>) lista;
        notifyDataSetChanged();
    }

    public void restoreItem(Conversation chat, int position){
        converationsList.add(position, chat);
        notifyItemInserted(position);
    }

    public List<Conversation> getLista(){
        return converationsList;
    }


    @NonNull
    @Override
    public ConversationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater v = LayoutInflater.from(parent.getContext()); //creo el LayoutInflater
        ConversationHolder m = new ConversationHolder(v.inflate(R.layout.card_chat, parent, false));
        return m;
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationHolder holder, int position) {
        holder.imprimir(position);
    }

    @Override
    public int getItemCount() {
        return converationsList.size();
    }

    public class ConversationHolder extends RecyclerView.ViewHolder {
        private TextView inicialesChat, nombreChat, mensajeChat, horaChat, notifChat;
        private CardView cv;
        public RelativeLayout layoutDeleteCard,viewCardChat;

        public ConversationHolder(@NonNull View itemView) {
            super(itemView);
            this.inicialesChat = itemView.findViewById(R.id.inicialesUser);
            this.nombreChat = itemView.findViewById(R.id.nameUser);
            this.mensajeChat = itemView.findViewById(R.id.textoChat);
            this.horaChat = itemView.findViewById(R.id.horaChat);
            this.notifChat = itemView.findViewById(R.id.notificacionChat);
            this.cv = itemView.findViewById(R.id.showChat);
            this.layoutDeleteCard = itemView.findViewById(R.id.deleteCardChat);
            this.viewCardChat = itemView.findViewById(R.id.viewCardChat);
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Abriendo Chat", Toast.LENGTH_SHORT).show();
                    interfaceConversation.openConversation(converationsList.get(getLayoutPosition()));
                    converationsList.get(getLayoutPosition()).setUnreadCount(0);
                    notifyItemChanged(getLayoutPosition());
                }
            });
        }

        public void imprimir(int position) {
            Conversation c = converationsList.get(position);
            String[] nombre = c.getLabel().split(" ");
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
            inicialesChat.setText(iniciales);
            nombreChat.setText(c.getLabel());
            String msg = c.getLastMsg();
            if (msg.length() > 20) {
                msg = msg.substring(0, 20) + "...";
            }
            mensajeChat.setText(msg);
            Date hora = new Date(c.getTimeMsg());
            horaChat.setText(hora.getHours() + ":" + (hora.getMinutes() < 10 ? "0"+ hora.getMinutes(): hora.getMinutes()));
            if (c.getUnreadCount() > 0) {
                viewCardChat.setBackground(itemView.getResources().getDrawable(R.color.backNotif));
                notifChat.setAlpha(1);
            } else {
                viewCardChat.setBackground(itemView.getResources().getDrawable(R.color.backCardChat));
                notifChat.setAlpha(0);
            }
            notifChat.setText(c.getUnreadCount() + "");
        }
    }




}

