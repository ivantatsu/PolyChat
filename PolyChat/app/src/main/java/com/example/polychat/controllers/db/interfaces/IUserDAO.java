package com.example.polychat.controllers.db.interfaces;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.polychat.models.User;

import java.util.List;

@Dao
public interface IUserDAO {
    @Query("SELECT * FROM POLY_USERS ORDER BY id")
    public LiveData<List<User>> getAllUsers();

    @Query("SELECT * FROM POLY_USERS WHERE userId = :id")
    public User getUser(String id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void addUser(User contacto);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAll(List<User> users);

    @Update (onConflict = OnConflictStrategy.IGNORE)
    public void updateUser(User contacto);

    @Query("DELETE FROM POLY_USERS WHERE id = :id")
    public void deleteUser(String id);

    @Query("UPDATE POLY_USERS SET status = :status WHERE userId = :userId")
    public void changeState(String userId, Integer status);

    @Query("DELETE FROM POLY_USERS WHERE userId != :userId")
    public void deleteUsers(String userId);
}


