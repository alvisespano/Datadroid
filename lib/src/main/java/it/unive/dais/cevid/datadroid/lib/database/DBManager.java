package it.unive.dais.cevid.datadroid.lib.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.concurrent.ExecutionException;

import it.unive.dais.cevid.datadroid.lib.database.event.QueryEvent;
import it.unive.dais.cevid.datadroid.lib.parser.CsvParser;
import it.unive.dais.cevid.datadroid.lib.parser.ParserException;
import it.unive.dais.cevid.datadroid.lib.util.AsyncTaskResult;
import it.unive.dais.cevid.datadroid.lib.util.Function;
import it.unive.dais.cevid.datadroid.lib.util.MapManager;

public class DBManager {
    private AppDatabase database;
    private DBIOScheduler scheduler = new DBIOScheduler(this);
    private static DBManager db;
    private boolean firstTime = false;
    private final Boolean lock = Boolean.TRUE;
    private boolean opened = false;

    public static DBManager instance(){
        if(db == null)
            db = new DBManager();
        return db;
    }

    private DBManager(){}


    public void insert(MapEntity m){
        scheduler.insert(new QueryEvent("insert", database.getDataDao(), MapEntity.class), m);
    }
    public void insert(MapEntity m, MapManager mm, @NonNull Function<MarkerOptions, MarkerOptions> optf){
        try {
            mm.putMarkerFromMapItem(m, optf);
        } catch (Exception e) {
            e.printStackTrace();
        }
        insert(m);
    }
    private void initialize(MapEntity m, DBIOScheduler.Priority priority){
        scheduler.insert(new QueryEvent("insert", database.getDataDao(), MapEntity.class), priority, m);
    }
    private void initialize(MapEntity[] mm, DBIOScheduler.Priority priority){
        scheduler.insert(new QueryEvent("insertAll", database.getDataDao(), MapEntity.class), priority, (Object) mm);
    }

    public void insert(MapEntity[] ms){
        scheduler.insert(new QueryEvent("insertAll", database.getDataDao(), MapEntity.class), (Object) ms);
    }

    public List<MapEntity> getAll(){
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
                //Log.i("DBManager", "Query processed!");
            }
            return (List<MapEntity>) query.getResult();
        }
    }

    //dummy transaction -> do a fake transaction
    private void dummyTransaction(){
        database.beginTransaction();
        database.endTransaction();
    }
    public DBBuilder builder(Context context, String name){
        return new DBBuilder(context, name);
    }
    public static class DBBuilder{
        private final String name;
        private Context context = null;
        private CsvParser parser = null;
        private String colTitle;
        private String colLon;
        private String colLat;
        private Function<MarkerOptions, MarkerOptions> optf;
        private String colDescr;
        private MapManager mm;

        private DBBuilder(Context context, String name) {
            this.context = context;
            this.name = name;
        }

        /**
         * Questo metodo permette di caricare, in fase di apertura/creazione del database, i marker
         * memorizzati nel database all'interno della mappa.
         * @param mm
         * @param optf
         * @return
         */
        public DBBuilder withGMap(MapManager mm, @NonNull Function<MarkerOptions, MarkerOptions> optf){
            this.optf = optf;
            this.mm = mm;
            return this;
        }
        public DBBuilder withParser(CsvParser parser, String colTitle, String colDescr, String colLat, String colLon){
            this.parser = parser;
            this.colTitle = colTitle;
            this.colDescr = colDescr;
            this.colLat = colLat;
            this.colLon = colLon;
            return this;
        }
        public DBManager build(){
            return new DBManager().buildDatabase(context, name, mm, optf, parser, colTitle, colDescr, colLat, colLon);
        }

    }

    /**
     * buildDatabase: inizializza il database con i parametri richiesti.
     * Inoltre avvia lo scheduler, che lavora in un Thread separato e gestisce le interazioni con il Database.
     * @param context
     * @param name
     * @param mm
     * @param optf
     * @param parser
     * @param colTitle
     * @param colDescr
     * @param colLat
     * @param colLon
     * @return
     */
    private DBManager buildDatabase(Context context, String name, MapManager mm, Function<MarkerOptions, MarkerOptions> optf, CsvParser parser, String colTitle, String colDescr, String colLat, String colLon) {

            scheduler.start();
            database = Room.databaseBuilder(context, AppDatabase.class, name)
                    .addCallback(new RoomDatabase.Callback() {
                        /**
                         * Questo metodo viene chiamato solo quando il database viene creato (quando vengono create le tabelle).
                         *
                         * @param db
                         */
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            firstTime = true;
                            Log.i("DB", "Created...");
                        }

                        /**
                         * Questo metodo viene chiamato all'apertura del database: viene controllato se
                         * il database è stato appena creato, e se lo è vengono inseriti i Marker nella mappa (se espresso dal programmatore).
                         * Se non è stato creato, viene effettuata una query che ritorna tutti i Marker memorizzati nel database e gli inserisce nella mappa,
                         * se il programmatore lo ha deciso.
                         * @param db
                         */
                        @Override
                        public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                super.onOpen(db);

                                Log.i("DB", "Opened...");
                                AsyncTaskResult.run(() -> {
                                    if (firstTime && parser != null) {
                                        firstTime = false;
                                        try {
                                            List<CsvParser.Row> rows = parser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
                                            Log.i("DB", "Rows size: " + rows.size());
                                            for (CsvParser.Row r : rows) {
                                                MapEntity m = new MapEntity(r.get(colTitle), r.get(colDescr), r.get(colLat), r.get(colLon));
                                                //Toast.makeText(this, "Number of entities: " + list.size(), Toast.LENGTH_LONG).show();
                                                //Log.i("DB", r.get(colTitle) + ", " + r.get(colDescr) + ", " + r.get(colLat) + ", " + r.get(colLon));
                                                initialize(m, DBIOScheduler.Priority.HIGH);
                                                if(mm != null && optf != null)
                                                    insertOnMaps(m, mm, optf);
                                                //Log.i("DB", "SIZE: "+getAll().size());
                                                //Log.i("DB", "Row inserted!");
                                            }
                                        } catch (InterruptedException | ExecutionException | ParserException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    else if(mm != null && optf != null){
                                        List<MapEntity> ms = getAll();
                                        insertOnMaps(ms, mm, optf);
                                    }
                                    Log.i("DB", "Releasing lock..");

                                    opened = true;
                                    Log.i("DB", "lock released! Opened = "+opened);
                                });

                        }
                    }).allowMainThreadQueries().build();
        dummyTransaction();
        return this;
    }
    public void insertOnMaps(List<MapEntity> ms, MapManager mm,  Function<MarkerOptions, MarkerOptions> optf){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> mm.putMarkersFromMapItems(ms, optf));

    }
    public void insertOnMaps(MapEntity ms, MapManager mm,  Function<MarkerOptions, MarkerOptions> optf){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            try {
                mm.putMarkerFromMapItem(ms, optf);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }
}

