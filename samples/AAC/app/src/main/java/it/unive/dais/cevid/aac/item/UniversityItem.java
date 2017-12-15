package it.unive.dais.cevid.aac.item;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.net.URL;
import java.util.List;

import it.unive.dais.cevid.datadroid.lib.util.MapItem;

/**
 * Created by Fonto on 04/09/17.
 */

public class UniversityItem extends AbstractItem implements MapItem, Serializable{
    private static final String CODICE_COMPARTO = "UNI";

    public UniversityItem(@NonNull String id, @NonNull String title, @NonNull String description, @NonNull String capite, double latitude, double longitude, @NonNull List<URL> urls) {
        super(id, title, description, capite, latitude, longitude, urls);
    }

    @NonNull
    @Override
    public String getCodiceComparto() {
        return CODICE_COMPARTO;
    }

}
