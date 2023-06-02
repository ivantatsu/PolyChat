package com.example.polychat.controllers.db.interfaces;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.polychat.models.Call;

import java.util.List;

@Dao
public interface ICallDAO {
    @Query("SELECT * FROM POLY_CALLS ORDER BY id")
    public LiveData<List<Call>> getAllCalls();

    @Query("SELECT * FROM POLY_CALLS WHERE id = :id")
    public Call getCall(String id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void addCall(Call contacto);

    @Update (onConflict = OnConflictStrategy.IGNORE)
    public void updateCall(Call contacto);

    @Query("DELETE FROM POLY_CALLS WHERE id = :id")
    public void deleteCall(String id);
}


