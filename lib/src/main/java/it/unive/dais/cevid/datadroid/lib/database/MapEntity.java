package it.unive.dais.cevid.datadroid.lib.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import it.unive.dais.cevid.datadroid.lib.util.MapItem;


/*
TODO: Entity estende MapItem?

 */
@Entity(tableName = "mapentities")
public class MapEntity implements MapItem {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private String id;
    @ColumnInfo(name = "lat")
    private double lat;
    @ColumnInfo(name = "lon")
    private double lon;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "description")
    private String description;


    public MapEntity(String title, String description, double lat, double lon){
        this.lat = lat;
        this.lon = lon;
        this.description = description;
        this.title = title;
    }
    public MapEntity(){}

    @NonNull
    @Override
    public LatLng getPosition(){
        return new LatLng(lat, lon);
    }

    @NonNull
    @Override
    public String getTitle() {
        return title;
    }

    @NonNull
    @Override
    public String getDescription(){
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
    public String getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
    public void setId(String id) {
        this.id = id;
    }
}
