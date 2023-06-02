package com.example.polychat.controllers.db.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.polychat.controllers.db.interfaces.ICallDAO;
import com.example.polychat.models.Call;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Call.class}, version = 1, exportSchema = false)

public abstract class CallRoom extends RoomDatabase{

    public abstract ICallDAO getCallDAO();

    private static final String DATABASE_NAME = "POLY_CALLS";

    private static CallRoom INSTANCE;

    private static final int THREADS = 4;

    public static final ExecutorService dbExecutor = Executors.newFixedThreadPool(THREADS);

    public static CallRoom getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (CallRoom.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(), CallRoom.class,
                                    DATABASE_NAME)
                            .addCallback(mRoomCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final Callback mRoomCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            dbExecutor.execute(() -> {
                ICallDAO dao = INSTANCE.getCallDAO();
                // CARGAR MENSAJES NUEVA SESSION.
            });

        }
    };
}
