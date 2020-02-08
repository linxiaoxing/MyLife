package com.example.note.data.dbhelper;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.mylife.base.App;
import com.example.note.data.dao.ImageDAO;
import com.example.note.data.dao.NoteDAO;
import com.example.note.data.dao.SoundDAO;
import com.example.note.data.entity.Image;
import com.example.note.data.entity.Note;
import com.example.note.data.entity.Sound;

@Database(entities = {Note.class, Sound.class, Image.class}, version = 1, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase{
    private static final String DATABASE_NAME = "db_note.db";
    private static AppDatabase ourInstance = null;

    public abstract NoteDAO noteDAO();

    public abstract SoundDAO soundDAO();

    public abstract ImageDAO imageDAO();

    public static AppDatabase getInstance() {
        if (ourInstance == null) {
            synchronized (AppDatabase.class) {
                if (ourInstance == null) {
                    ourInstance = Room.databaseBuilder(App.getAppContext(), AppDatabase.class, DATABASE_NAME).build();
                }
            }
        }
        return ourInstance;
    }

    @Override
    public void close() {
        super.close();
        ourInstance = null;
    }
}
