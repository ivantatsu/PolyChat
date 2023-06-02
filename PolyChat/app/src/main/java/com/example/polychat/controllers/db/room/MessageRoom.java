package com.example.polychat.controllers.db.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.polychat.controllers.db.interfaces.IMessageDAO;
import com.example.polychat.models.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Message.class}, version = 1, exportSchema = false)

public abstract class MessageRoom extends RoomDatabase{

    public abstract IMessageDAO getMessageDAO();

    private static final String DATABASE_NAME = "POLY_MESSAGES";

    private static MessageRoom INSTANCE;

    private static final int THREADS = 4;

    public static final ExecutorService dbExecutor = Executors.newFixedThreadPool(THREADS);

    public static MessageRoom getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (MessageRoom.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(), MessageRoom.class,
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
        }
    };
}
