package it.unive.dais.cevid.datadroid.lib.util;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

import it.unive.dais.cevid.datadroid.lib.parser.CsvRowParser;
import it.unive.dais.cevid.datadroid.lib.parser.ParserException;

/**
 * Rappresenta un oggetto visualizzabile sulla mappa, con le informazioni minime per il posizionamento e la creazione di un marker.
 */
public interface MapItem extends Serializable {
    /**
     * Ritorna la posizione.
     *
     * @param longitudeColumName
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

    static Function<CsvRowParser.Row, MapItem> factoryByCsvNames(String latitudeColumName, String longitudeColumName, String titleColumnName, String descriptionColumnName) {
        return new Function<CsvRowParser.Row, MapItem>() {
            @Override
            public MapItem apply(CsvRowParser.Row r) {
                return new MapItem() {
                    @Override
                    public LatLng getPosition() throws ParserException {
                        String lat = r.get(latitudeColumName), lng = r.get(longitudeColumName);
                        return new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                    }

                    @Override
                    public String getTitle() throws ParserException {
                        return r.get(titleColumnName);
                    }

                    @Override
                    public String getDescription() throws ParserException {
                        return r.get(descriptionColumnName);
                    }
                };
            }
        };
    }
}
