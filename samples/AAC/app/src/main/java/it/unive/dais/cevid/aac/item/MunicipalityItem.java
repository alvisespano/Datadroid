package it.unive.dais.cevid.aac.item;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.net.URL;
import java.util.List;

import it.unive.dais.cevid.datadroid.lib.util.MapItem;

/**
 * Created by fbusolin on 13/11/17.
 */

public class MunicipalityItem extends MapItem implements Serializable {
    private String title;
    private double latitude;
    private double longitude;
    private String description;
    private String codiceEnte;
    List<URL> urls;

    private static final String codice_comparto = "PRO";

    public MunicipalityItem(String title, double latitude, double longitude, String description, List<URL> urls, String codiceEnte) {
        this.codiceEnte = codiceEnte;
        this.description = description;
        this.title = title;
        this.urls = urls;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public String getCodiceEnte() {
        return codiceEnte;
    }

    public String getCodiceComparto() {
        return codice_comparto;
    }

    public List<URL> getUrls() {
        return urls;
    }

}
