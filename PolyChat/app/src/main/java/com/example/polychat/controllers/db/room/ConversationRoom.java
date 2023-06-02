package com.example.polychat.controllers.db.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.polychat.controllers.db.interfaces.IConversationDAO;
import com.example.polychat.models.Conversation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Conversation.class}, version = 2, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class ConversationRoom extends RoomDatabase{

    public abstract IConversationDAO getConversationDAO();

    private static final String DATABASE_NAME = "POLY_CONVERSATIONS";

    private static ConversationRoom INSTANCE;

    private static final int THREADS = 4;

    public static final ExecutorService dbExecutor = Executors.newFixedThreadPool(THREADS);

    public static ConversationRoom getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (ConversationRoom.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(), ConversationRoom.class,
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
