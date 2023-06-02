package com.example.polychat.controllers.db.interfaces;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.polychat.models.Conversation;

import java.util.List;

@Dao
public interface IConversationDAO {
    @Query("SELECT * FROM POLY_CONVERSATIONS ORDER BY id")
    public LiveData<List<Conversation>> getAllConversations();

    @Query("SELECT * FROM POLY_CONVERSATIONS WHERE id = :id")
    public Conversation getConversation(String id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void addConversation(Conversation conversation);

    @Update (onConflict = OnConflictStrategy.IGNORE)
    public void updateConversation(Conversation conversation);

    @Query("DELETE FROM POLY_CONVERSATIONS WHERE conversation_id = :id")
    public void deleteConversation(String id);
}


