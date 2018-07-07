package it.unive.dais.cevid.datadroid.lib.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import it.unive.dais.cevid.datadroid.lib.parser.CsvParser;

@Database(entities = {MapEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private Context context;
    public abstract MapEntityDao entityDao();

    public List<MapEntity> rebaseFrom(List<CsvParser.Row> CSVRow, String title, String description, String lat, String lon){
        entityDao().deleteAll();
        List<MapEntity> mapEntities = new ArrayList<>();
        for(CsvParser.Row r : CSVRow){
            try {
                mapEntities.add(new MapEntity(r.get(title), r.get(description), Double.parseDouble(r.get(lat)), Double.parseDouble(r.get(lon))));
            }catch(Exception e){

            }
        }
        return mapEntities;
    }
}