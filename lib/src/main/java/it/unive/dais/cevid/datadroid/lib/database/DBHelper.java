package it.unive.dais.cevid.datadroid.lib.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateFormat;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.unive.dais.cevid.datadroid.lib.R;
import it.unive.dais.cevid.datadroid.lib.database.item.Entity;
import it.unive.dais.cevid.datadroid.lib.parser.AppaltiParser;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "datadroid_database.db";
    private static final int DATABSE_VERSION = 1;
    private static final int ANNO_APPALTI = 2016;
    private static final String TABLE_ENTE = "Ente";
    private static final String TABLE_BILANCIO = "Bilancio";
    private static final String TABLE_APPALTI = "Appalti";

    private static DBHelper instance = null;
    private Context context;

    private DBHelper(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABSE_VERSION);
        this.context = context.getApplicationContext();

        updateDatabase();
    }

    //Singleton stuff

    public static DBHelper getSingleton(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }

        return instance;
    }

    public static DBHelper getSingleton() {
        if (instance == null) {
            throw new IllegalStateException();
        }
        return instance;
    }

    //Creation stuff

    @Override
    public void onCreate(SQLiteDatabase db) {
        setCreationDate();
        insertFromFile(db);
    }

    private void insertFromFile(SQLiteDatabase db) {
        try {
            insertFromFile(db, R.raw.database_enti_init);
            insertFromFile(db, R.raw.database_ba_init);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void insertFromFile(SQLiteDatabase db, int resource) throws IOException {
        InputStream insertsStream = context.getResources().openRawResource(resource);
        BufferedReader insertReader = new BufferedReader(new InputStreamReader(insertsStream));

        while (insertReader.ready()) {
            String insertStmt = insertReader.readLine();

            if (!insertStmt.equals("")) {
                db.execSQL(insertStmt);
            }
        }
        insertReader.close();
    }

    private void setCreationDate() {
        try {
            Date currentDate = new Date();
            DataOutputStream dataOutputStream = new DataOutputStream(context.openFileOutput("db_creation_time.txt", Context.MODE_PRIVATE));
            dataOutputStream.writeLong(currentDate.getTime());
            dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Upgrade stuff

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        deleteTables(db);
        onCreate(db);
    }

    private void deleteTable(SQLiteDatabase db, String tableName) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        try {
            insertFromFile(db, R.raw.database_enti_init);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteTables(SQLiteDatabase db) {
        deleteTable(db, TABLE_ENTE);
        deleteTable(db, TABLE_BILANCIO);
        deleteTable(db, TABLE_APPALTI);
    }

    //Update stuff

    private void updateDatabase() {
        File dbFile = context.getDatabasePath(DATABASE_NAME);

        if (dbFile.exists() && isOldDatabase()) {
            deleteTable(TABLE_BILANCIO);
        }
    }

    private long getCreationTimestamp() {
        long lastTime = 0L;
        try {
            DataInputStream dataInputStream = new DataInputStream(context.openFileInput("db_creation_time.txt"));
            lastTime = dataInputStream.readLong();
            dataInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lastTime;
    }

    private Calendar getTimestampCal(long timestamp) {
        Calendar timeStampCal = Calendar.getInstance(Locale.ITALIAN);
        timeStampCal.setTimeInMillis(timestamp);

        return timeStampCal;
    }

    public boolean isOldDatabase() {
        Calendar creationCalendar = Calendar.getInstance(Locale.ITALIAN);
        Calendar currentCalendar = Calendar.getInstance(Locale.ITALIAN);

        creationCalendar.setTimeInMillis(getCreationTimestamp());
        currentCalendar.setTimeInMillis(System.currentTimeMillis());

        return (creationCalendar.get(Calendar.MONTH) < currentCalendar.get(Calendar.MONTH))? true : false;
    }

    private void deleteTable(String tableName) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        try {
            insertFromFile(db, R.raw.database_enti_init);
        } catch (IOException e) {
            e.printStackTrace();
        }
        db.close();
    }

    //Data-insertion stuff

    //Bilancio-insertion stuff

    private void insertBilancio(String codiceEnte, List<SoldipubbliciParser.Data> soldiPubbliciList) {
        SQLiteDatabase db = getWritableDatabase();
        for (SoldipubbliciParser.Data data : soldiPubbliciList) {
            insertVoceBilancio(db, data, "2015", data.importo_2015);
            insertVoceBilancio(db, data, "2016", data.importo_2016);
            insertVoceBilancio(db, data, "2017", data.importo_2017);
        }
        db.close();
    }

    private void insertVoceBilancio (SQLiteDatabase db, SoldipubbliciParser.Data voceBilancio, String anno, String importo) {
        ContentValues values = new ContentValues();

        values.put("codice_siope", voceBilancio.codice_siope);
        values.put("codice_ente", voceBilancio.cod_ente);
        values.put("anno", anno);
        values.put("descrizione_codice", voceBilancio.descrizione_codice);
        values.put("importo", importo);

        db.insert("Bilancio", null, values);
    }

    //Appalti-insertion stuff

    private void insertTenders(String codiceEnte, List<AppaltiParser.Data> tendersList) {
        SQLiteDatabase db = getWritableDatabase();

        for (AppaltiParser.Data appalto : tendersList) {
            if (!appalto.aggiudicatario.equals("Dati assenti o mal formattati")) {
                try {
                    db.insertOrThrow("Appalti", null, generateNewAppaltoRecord(appalto, codiceEnte));
                } catch (android.database.sqlite.SQLiteConstraintException e) {
                    updateImportoAppalti(db, appalto);
                }
            }
        }
        db.close();
    }

    private ContentValues generateNewAppaltoRecord(AppaltiParser.Data appalto, String codiceEnte) {
        ContentValues values = new ContentValues();

        values.put("cig", appalto.cig);
        values.put("oggetto", appalto.oggetto);
        values.put("scelta_contraente", appalto.sceltac);
        values.put("codice_fiscale_aggiudicatario", appalto.codiceFiscaleAgg);
        values.put("aggiudicatario", appalto.aggiudicatario);
        values.put("importo", appalto.importo);
        values.put("codice_ente", codiceEnte);

        return values;
    }

    private void updateImportoAppalti(SQLiteDatabase db, AppaltiParser.Data appalto) {
        String query = "SELECT importo FROM Appalti WHERE cig=? AND oggetto=? AND codice_fiscale_aggiudicatario=? ;";
        Cursor cursor = db.rawQuery(query, new String[]{appalto.cig, appalto.oggetto, appalto.codiceFiscaleAgg});
        cursor.moveToFirst();

        double old_importo = cursor.getDouble(0);
        cursor.close();

        String updateQuery = "UPDATE Appalti SET importo=? WHERE cig=? AND oggetto=? AND codice_fiscale_aggiudicatario=? ;";
        db.rawQuery(updateQuery, new String[]{new Double(old_importo + appalto.importo).toString(), appalto.cig, appalto.oggetto, appalto.aggiudicatario});

    }

    public List<String> getEntitie() {
        SQLiteDatabase db = getReadableDatabase();

        List<String> entities = new ArrayList<>();

        String query = "SELECT codice_ente FROM Ente";

        Cursor cursor = db.rawQuery(query, new String[]{});

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            entities.add(cursor.getString(0));
            cursor.moveToNext();
        }

        db.close();

        return entities;
    }
}