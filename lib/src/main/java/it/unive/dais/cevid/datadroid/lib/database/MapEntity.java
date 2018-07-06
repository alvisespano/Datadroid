package it.unive.dais.cevid.datadroid.lib.database;

import android.arch.persistence.room.ColumnInfo;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import it.unive.dais.cevid.datadroid.lib.util.MapItem;

@android.arch.persistence.room.Entity
/*
TODO: Entity estende MapItem?

 */
public class MapEntity implements MapItem {
    @ColumnInfo(name = "lat")
    private double lat;
    @ColumnInfo(name = "lon")
    private double lon;
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "description")
    private String description;

    @NonNull
    @Override
    public LatLng getPosition() throws Exception {
        return new LatLng(lat, lon);
    }

    @NonNull
    @Override
    public String getTitle() throws Exception {
        return title;
    }

    @NonNull
    @Override
    public String getDescription() throws Exception {
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
}
