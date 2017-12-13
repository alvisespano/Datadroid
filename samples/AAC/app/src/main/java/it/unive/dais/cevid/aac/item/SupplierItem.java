package it.unive.dais.cevid.aac.item;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.unive.dais.cevid.aac.parser.SupplierParser;
import it.unive.dais.cevid.datadroid.lib.util.MapItem;

/**
 * Created by fbusolin on 13/11/17.
 */
public class SupplierItem implements MapItem, Serializable {
    @NonNull
    private final SupplierParser.Data data;
    @NonNull
    private final Position position;

    public SupplierItem(Context context, @NonNull SupplierParser.Data data) {
        this.data = data;
        LatLng ll = getLatLngFromAddress(context, getAddress());
        this.position = new Position(ll.latitude, ll.longitude);
    }

    private LatLng getLatLngFromAddress(Context context, String address) {
        Geocoder geocode = new Geocoder(context, Locale.getDefault());
        List<Address> names = new ArrayList<>();
        try {
            names = geocode.getFromLocationName(address, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (names.size() <= 0) return new LatLng(0, 0);
        return new LatLng(names.get(0).getLatitude(), names.get(0).getLongitude());
    }

    @NonNull
    public String getType() {
        return data.forma_societaria;
    }

    @Override
    @NonNull
    public LatLng getPosition() {
        return position.getLatLng();
    }

    @Override
    @NonNull
    public String getTitle() {
        return data.ragione_sociale;
    }

    @NonNull
    public String getAddress() {
        return String.format("%s %s %s %s %s", data.indirizzo, data.comune, data.provincia, data.regione, data.nazione);
    }

    @NonNull
    public String getPiva() {
        return data.piva;
    }

    @Override
    @NonNull
    public String getDescription() {
        return String.format("%s, %s",data.ragione_sociale,data.forma_societaria);
    }

    // serve solo per serializzare un LatLng.
    public class Position implements Serializable{
        private final double latitude, longitude;

        Position(double latitude, double longitude){
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @NonNull
        LatLng getLatLng(){
            return new LatLng(latitude, longitude);
        }
    }
    @Override
    public String toString(){
        return this.getTitle();
    }

}
