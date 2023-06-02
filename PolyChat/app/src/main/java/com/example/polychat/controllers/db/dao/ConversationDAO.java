package com.example.polychat.controllers.db.dao;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.polychat.controllers.db.interfaces.IConversationDAO;
import com.example.polychat.controllers.db.room.ConversationRoom;
import com.example.polychat.controllers.db.room.MessageRoom;
import com.example.polychat.models.Conversation;

import java.util.List;

public class ConversationDAO extends AndroidViewModel {

    private final IConversationDAO iConversationDAO;
    private LiveData<List<Conversation>> conversationList;

    public ConversationDAO(@NonNull Application application) {
        super(application);
        ConversationRoom db = ConversationRoom.getInstance(application.getApplicationContext());
        iConversationDAO = db.getConversationDAO();
        conversationList = iConversationDAO.getAllConversations();
    }

    public LiveData<List<Conversation>> getAllConversations() {
        return conversationList;
    }


    public Conversation getConversation(String id){
        return iConversationDAO.getConversation(id);
    }

    public void getConversations(){
        MessageRoom.dbExecutor.execute(()->conversationList = iConversationDAO.getAllConversations());
    }

    public void addConversation(Conversation conversation){
        MessageRoom.dbExecutor.execute(()-> iConversationDAO.addConversation(conversation));
    }

    public void updateConversation(Conversation conversation){
        MessageRoom.dbExecutor.execute(()-> iConversationDAO.updateConversation(conversation));
    }

    public void deleteConversation(String id) {
        MessageRoom.dbExecutor.execute(()-> iConversationDAO.deleteConversation(id));
    }
}
