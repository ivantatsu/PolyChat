package com.example.polychat.controllers.db.dao;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.polychat.controllers.db.interfaces.ICallDAO;
import com.example.polychat.controllers.db.room.CallRoom;
import com.example.polychat.controllers.db.room.MessageRoom;
import com.example.polychat.models.Call;

import java.util.List;

public class CallDAO extends AndroidViewModel {

    private final ICallDAO iCallDAO;
    private LiveData<List<Call>> callList;

    public CallDAO(@NonNull Application application) {
        super(application);
        CallRoom db = CallRoom.getInstance(application.getApplicationContext());
        iCallDAO = db.getCallDAO();
        callList = iCallDAO.getAllCalls();
    }

    public LiveData<List<Call>> getAllCalls() {
        return callList;
    }


    public Call getCall(String id){
        return iCallDAO.getCall(id);
    }

    public void cargarCall(){
        MessageRoom.dbExecutor.execute(()->callList = iCallDAO.getAllCalls());
    }

    public void addCall(Call call){
        MessageRoom.dbExecutor.execute(()-> iCallDAO.addCall(call));
    }

    public void updateCall(Call call){
        MessageRoom.dbExecutor.execute(()-> iCallDAO.updateCall(call));
    }

    public void deleteCall(String id) {
        MessageRoom.dbExecutor.execute(()-> iCallDAO.deleteCall(id));
    }
}
