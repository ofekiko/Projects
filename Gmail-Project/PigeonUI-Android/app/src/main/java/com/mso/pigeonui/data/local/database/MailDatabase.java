package com.mso.pigeonui.data.local.database;

// Import Libraries
import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// Import Classes
import com.mso.pigeonui.R;
import com.mso.pigeonui.data.local.dao.LabelDao;
import com.mso.pigeonui.model.LabelEntity;
import com.mso.pigeonui.model.MailEntity;
import com.mso.pigeonui.data.local.dao.MailDao;

// The class of room data base:
@Database(entities = {MailEntity.class, LabelEntity.class}, version = 13)
public abstract class MailDatabase extends RoomDatabase {

    // Singleton
    private static volatile MailDatabase instance;

    // Return the DAO of the mails and labels
    public abstract MailDao mailDao();
    public abstract LabelDao labelDao();

    // Creating the database:
    public static synchronized MailDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            MailDatabase.class,
                            context.getString(R.string.mail_database)
                            )
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return instance;
    }
}

