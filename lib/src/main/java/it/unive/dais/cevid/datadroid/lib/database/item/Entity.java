package it.unive.dais.cevid.datadroid.lib.database.item;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

public class Entity {
    @NonNull
    private final String id, codiceComparto, name, bilancio, appalti;
    private final double latitude, longitude;
    private final int capite;

    public Entity(@NonNull String id, @NonNull String codiceComparto, @NonNull String name,
                  double latitude, double longitude, int capite, @NonNull String bilancio, @NonNull String appalti) {
        this.id = id;
        this.codiceComparto = codiceComparto;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.capite = capite;
        this.bilancio = bilancio;
        this.appalti = appalti;
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

    public int getCapite() {
        return capite;
    }

    @NonNull
    public String getBilancio() {
        return bilancio;
    }

    @NonNull
    public String getAppalti() {
        return appalti;
    }

    @Override
    public String toString(){
        return this.name;
    }
}