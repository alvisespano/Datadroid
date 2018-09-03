package it.unive.dais.cevid.datadroid.lib.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.support.annotation.MainThread;

@Database(entities = MapEntity.class, version = 1)
public abstract class AppDatabase<T extends MapEntity> extends RoomDatabase {
    public abstract BaseDataDao  getDataDao();
}
