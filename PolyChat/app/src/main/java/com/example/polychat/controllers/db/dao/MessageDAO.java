package com.example.polychat.controllers.db.dao;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.polychat.controllers.db.interfaces.IMessageDAO;
import com.example.polychat.controllers.db.room.MessageRoom;
import com.example.polychat.models.Message;

import java.util.List;

public class MessageDAO extends AndroidViewModel {

    private final IMessageDAO iMessageDAO;
    private LiveData<List<Message>> listaMessages;

    public String getIdConversation() {
        return idConversation;
    }

    public void setIdConversation(String idConversation) {
        this.idConversation = idConversation;
        listaMessages = iMessageDAO.getAllMessages(this.idConversation);
    }

    private String idConversation;

    public MessageDAO(@NonNull Application application) {
        super(application);
        MessageRoom db = MessageRoom.getInstance(application.getApplicationContext());
        iMessageDAO = db.getMessageDAO();
    }

    public LiveData<List<Message>> getAllMessage() {
        return listaMessages;
    }


    public Message getMessage(String id){
        return iMessageDAO.getMessage(id);
    }

    public void getMessages(){
        if(listaMessages != null &&listaMessages.getValue() != null ){
            listaMessages.getValue().clear();
        }
        MessageRoom.dbExecutor.execute(()->listaMessages = iMessageDAO.getAllMessages(idConversation));
    }

    public void addMessage(Message message){
        MessageRoom.dbExecutor.execute(()-> iMessageDAO.addMessage(message));
    }

    public void updateMessage(Message message){
        MessageRoom.dbExecutor.execute(()-> iMessageDAO.updateMessage(message));
    }

    public void deleteMessage(String id) {
        MessageRoom.dbExecutor.execute(()-> iMessageDAO.deleteMessage(id));
    }
}
