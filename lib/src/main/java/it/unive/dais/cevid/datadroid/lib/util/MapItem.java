package it.unive.dais.cevid.datadroid.lib.util;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

import it.unive.dais.cevid.datadroid.lib.parser.CsvParser;
import it.unive.dais.cevid.datadroid.lib.parser.ParserException;

/**
 * Rappresenta un oggetto visualizzabile sulla mappa, con le informazioni minime per il posizionamento e la creazione di un marker.
 */
public interface MapItem extends Serializable {
    /**
     * Ritorna la posizione.
     *
     * @return la posizione in un oggetto di tipo LatLng.
     */
    LatLng getPosition() throws Exception;

    /**
     * Ritorna il titolo, o il nome, dell'item.
     *
     * @return il nome.
     */
    String getTitle() throws Exception;

    /**
     * Ritorna la descrizione.
     *
     * @return la descrizione.
     */
    String getDescription() throws Exception;

    static Function<CsvParser.Row, MapItem> byCsvColumnNames(String latitude, String longitude, String title, String description) {
        return row -> new MapItem() {
            @Override
            public LatLng getPosition() throws ParserException {
                String lat = row.get(latitude), lng = row.get(longitude);
                return new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
            }

            @Override
            public String getTitle() throws ParserException {
                return row.get(title);
            }

            @Override
            public String getDescription() throws ParserException {
                return row.get(description);
            }
        };
    }
}
