package it.unive.dais.cevid.datadroid.lib.database;

import android.arch.persistence.room.Room;
import android.content.Context;

public class DatabaseBuilder {

    public static AppDatabase build(Context context, String databaseName){
        return Room.databaseBuilder(context, AppDatabase.class, databaseName).build();
    }

}
