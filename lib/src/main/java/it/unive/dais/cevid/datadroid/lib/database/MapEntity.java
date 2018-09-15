package it.unive.dais.cevid.datadroid.lib.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import it.unive.dais.cevid.datadroid.lib.util.MapItem;

@Entity
public class MapEntity implements MapItem{


    /*Attributes*/
    @PrimaryKey(autoGenerate = true)
    private int id = 0;
    @ColumnInfo(name = "lat")
    private double lat;
    @ColumnInfo(name = "lon")
    private double lon;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "description")
    private String description;

    public int getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }


    public double getLon() {
        return lon;
    }


    @NonNull
    @Override
    public LatLng getPosition() throws Exception {
        return new LatLng(lat, lon);
    }

    @NonNull
    public String getTitle() {
        return title;
    }



    @NonNull
    public String getDescription() {
        return description;
    }

    public MapEntity(String title, String description, double lat, double lon){
        this.title = title;
        this.description = description;
        this.lat = lat;
        this.lon = lon;
    }

    public MapEntity(String title, String description, String lat, String lon){
        this(title, description, Double.parseDouble(lat), Double.parseDouble(lon));
    }


    public void setId(int id) {
        this.id = id;
    }
}
