package it.unive.dais.cevid.datadroid.lib.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import it.unive.dais.cevid.datadroid.lib.util.AsyncTaskResult;

public class DBManager {
    private AppDatabase database;
    private boolean instantiated = false;
    private boolean initializing = false;

    public synchronized AppDatabase getDatabase(Context context) throws Exception {
        if(!initializing)
            throw new Exception("DBManager: DB not initialize! Call buildDatabase!");
        else {
            while (!instantiated)
                wait();
            return database;
        }
    }

    public synchronized AppDatabase buildDatabase(Context context, String name){
        initializing = true;
        database = Room.databaseBuilder(context, AppDatabase.class, name)
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        AsyncTaskResult.run(() -> {
                            Log.i("DBManager", "Inserting...");
                            database.getDataDao().insert(new MapEntity("test03", "descrizione", 3.23, 4.23));
                            database.getDataDao().insert(new MapEntity("test04", "descrizione", 10.23, 10.23));
                            database.getDataDao().insert(new MapEntity("test05", "descrizione", 10.23, 10.23));
                        });
                    }
                }).build();
        instantiated = true;
        notifyAll();
        return database;
    }

}
