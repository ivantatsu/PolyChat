package com.example.polychat.controllers.db.dao;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.polychat.controllers.db.interfaces.IUserDAO;
import com.example.polychat.controllers.db.room.MessageRoom;
import com.example.polychat.controllers.db.room.UserRoom;
import com.example.polychat.controllers.managers.ManagerUsers;
import com.example.polychat.models.User;

import java.util.List;

public class UserDAO extends AndroidViewModel {

    private final IUserDAO iUserDAO;
    private LiveData<List<User>> usersList;
    private User user;

    public UserDAO(@NonNull Application application) {
        super(application);
        UserRoom db = UserRoom.getInstance(application.getApplicationContext());
        iUserDAO = db.getUserDAO();
        usersList = iUserDAO.getAllUsers();
    }

    public LiveData<List<User>> getAllUsers() {
        return usersList;
    }
    public void setAllUsers(List<User> users){
        MessageRoom.dbExecutor.execute(()-> iUserDAO.insertAll(users));
    }
    public void setUserDB(String id){
         MessageRoom.dbExecutor.execute(()-> {user = iUserDAO.getUser(id);});
    }
    public User getUser() {
        return user;
    }

    public void checkUser(String id){
        MessageRoom.dbExecutor.execute(()->{
            User user = iUserDAO.getUser(id);
            if(user == null){
                ManagerUsers.getUser(ManagerUsers.getCurrentUser().getUserId(), new ManagerUsers.onManagerUsersListenerUser() {
                    @Override
                    public void onSuccess(User user) {
                        iUserDAO.addUser(user);
                    }
                });
            }
        });
    }

    public void getUsers(){
        MessageRoom.dbExecutor.execute(()->usersList = iUserDAO.getAllUsers());
    }

    public void addUser(User call){
        MessageRoom.dbExecutor.execute(()-> iUserDAO.addUser(call));
    }

    public void updateUser(User call){
        MessageRoom.dbExecutor.execute(()-> iUserDAO.updateUser(call));
    }

    public void deleteUser(String id) {
        MessageRoom.dbExecutor.execute(()-> iUserDAO.deleteUser(id));
    }

    public void changeStatus(String id, Integer status) {
        MessageRoom.dbExecutor.execute(()-> iUserDAO.changeState(id,status));
    }

    public void deleteUsers(String userId) {
        MessageRoom.dbExecutor.execute(()-> iUserDAO.deleteUsers(userId));
    }
}
