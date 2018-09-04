package it.unive.dais.cevid.datadroid.lib.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.List;

import it.unive.dais.cevid.datadroid.lib.database.event.QueryEvent;
import it.unive.dais.cevid.datadroid.lib.parser.CsvParser;
import it.unive.dais.cevid.datadroid.lib.util.AsyncTaskResult;

public class DBManager {
    private AppDatabase database;
    private DBIOScheduler scheduler = new DBIOScheduler(this);
    private final Boolean lock = Boolean.TRUE;
    private AppDatabase buildDatabase(Context context, String name){
        scheduler.start();
        database = Room.databaseBuilder(context, AppDatabase.class, name)
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        insert(new MapEntity("titolo", "descrizione", 23.4, 23.4), DBIOScheduler.Priority.HIGH);
                        Log.i("DB", "Created...");
                    }

                    @Override
                    public void onOpen(@NonNull SupportSQLiteDatabase db) {
                        super.onOpen(db);
                        Log.i("DB", "Opened...");
                        /*to do: insert on Map.*/
                        AsyncTaskResult.run(()-> {
                            ArrayList<MapEntity> ms = getAll();
                            for(MapEntity m : ms){
                                Log.i("DB", "Inserting on GMaps!");
                            }
                        });

                    }
                }).allowMainThreadQueries().build();
        database.beginTransaction();
        database.endTransaction();
        return database;
    }

    public void open(){
        scheduler.insert(new QueryEvent("open", database.getDataDao(), MapEntity.class));
    }

    public void insert(MapEntity m){
        scheduler.insert(new QueryEvent("insert", database.getDataDao(), MapEntity.class), m);
    }

    private void insert(MapEntity m, DBIOScheduler.Priority priority){
        scheduler.insert(new QueryEvent("insert", database.getDataDao(), MapEntity.class), priority, m);
    }

    public void insert(MapEntity[] ms){
        scheduler.insert(new QueryEvent("insert", database.getDataDao(), MapEntity.class), (Object[]) ms);
    }

    public ArrayList<MapEntity> getAll(){
        QueryEvent query = new QueryEvent("findAll", database.getDataDao());
        scheduler.insert(query);
        synchronized (query){
            while(!query.getProcessed()) {
                try {
                    Log.i("DBManager", "Waiting processing query... "+query.toString());
                    query.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.i("DBManager", "Query processed!");
            }
            return (ArrayList<MapEntity>) query.getResult();
        }
    }

    public DBBuilder builder(Context context, String name){
        return new DBBuilder(context, name);
    }
    public class DBBuilder{
        private final String name;
        private GoogleMap gmap = null;
        private Context context = null;
        private CsvParser parser = null;

        public DBBuilder(Context context, String name) {
            this.context = context;
            this.name = name;
        }

        public DBBuilder withGMap(GoogleMap gmap){
            this.gmap = gmap;
            return this;
        }
        public DBBuilder withParser(CsvParser parser){
            this.parser = parser;
            return this;
        }
        public DBManager build(){
            //TO DO

            return null;
        }

    }
}
