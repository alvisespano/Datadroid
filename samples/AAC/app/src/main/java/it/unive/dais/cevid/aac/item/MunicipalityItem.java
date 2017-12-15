package it.unive.dais.cevid.aac.item;

import android.support.annotation.NonNull;

import java.net.URL;
import java.util.List;

/**
 * Created by fbusolin on 13/11/17.
 */

public class MunicipalityItem extends AbstractItem {
    private static final String CODICE_COMPARTO = "PRO";

    public MunicipalityItem(@NonNull String id, @NonNull String title, @NonNull String description, String capite, double latitude, double longitude, @NonNull List<URL> urls) {
        super(id, title, description, capite, latitude, longitude, urls);
    }

    @NonNull
    @Override
    public String getCodiceComparto() {
        return CODICE_COMPARTO;
    }

}
