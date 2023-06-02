package com.example.polychat.controllers.db.interfaces;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.polychat.models.Message;

import java.util.List;

@Dao
public interface IMessageDAO {
    @Query("SELECT * FROM POLY_MESSAGES WHERE idConversation = :idConversation ORDER BY timeSend")
    public LiveData<List<Message>> getAllMessages(String idConversation);

    @Query("SELECT * FROM POLY_MESSAGES WHERE id = :id")
    public Message getMessage(String id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void addMessage(Message message);

    @Update (onConflict = OnConflictStrategy.IGNORE)
    public void updateMessage(Message message);

    @Query("DELETE FROM POLY_MESSAGES WHERE id = :id")
    public void deleteMessage(String id);
}


