package it.unive.dais.cevid.aac.item;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.net.URL;
import java.util.List;
import it.unive.dais.cevid.datadroid.lib.util.MapItem;

/**
 * Created by spano on 07/12/2017.
 */
public abstract class AbstractItem implements MapItem, Serializable {
    @NonNull
    private final String title, description, id, capite;
    private final double latitude, longitude;
    @NonNull
    private final List<URL> urls;

    public AbstractItem(@NonNull String id, @NonNull String title, @NonNull String description, @NonNull String capite, double latitude, double longitude, @NonNull List<URL> urls) {
        this.id = id;
        this.description = description;
        this.title = title;
        this.capite = capite;
        this.urls = urls;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public abstract String getCodiceComparto();

    @NonNull
    public String getCapite() {
        return capite;
    }

    @NonNull
    public List<URL> getUrls() {
        return urls;
    }

    @Override
    public String toString(){
        return this.title;
    }


}
