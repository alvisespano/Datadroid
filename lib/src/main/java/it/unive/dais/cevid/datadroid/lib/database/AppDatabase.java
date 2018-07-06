package it.unive.dais.cevid.datadroid.lib.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {MapEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MapEntityDao userDao();
}