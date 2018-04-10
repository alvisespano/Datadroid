package it.unive.dais.cevid.datadroid.lib.database.item;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

public class Entity {
    @NonNull
    private final String id, codiceComparto, name;
    private final double latitude, longitude;

    public Entity(@NonNull String id, @NonNull String codiceComparto, @NonNull String name, double latitude, double longitude) {
        this.id = id;
        this.codiceComparto = codiceComparto;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getCodiceComparto() {
        return codiceComparto;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }

    @Override
    public String toString(){
        return this.name;
    }
}