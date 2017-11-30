package it.unive.dais.cevid.aac.item;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.unive.dais.cevid.aac.parser.SupplierParser;
import it.unive.dais.cevid.aac.util.SupplierData;
import it.unive.dais.cevid.datadroid.lib.util.MapItem;

/**
 * Created by fbusolin on 13/11/17.
 */
//TODO: crash, NotSerializableException
public class SupplierItem extends MapItem implements Serializable {
    private final SupplierData data;
    private Position ll;

    public SupplierItem(Context context, SupplierData data) {
        this.data = data;
        LatLng ll = getLatLngFromAddress(context, getAddress());
        this.ll = new Position(ll.latitude, ll.longitude);
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

    public String getType() {
        return data.forma_societaria;
    }

    @Override
    public LatLng getPosition() {
        return ll.getLatLng();
    }

    @Override
    public String getTitle() {
        return data.ragione_sociale;
    }

    public String getAddress() {
        return String.format("%s %s %s %s %s", data.indirizzo, data.comune, data.provincia, data.regione, data.nazione);
    }

    public String getPiva() {
        return data.piva;
    }

    @Override
    public String getDescription() {
        return String.format("%s, %s",data.ragione_sociale,data.forma_societaria);
    }


    public class Position implements Serializable{
        private double latitude,longitude;
        public Position(double latitude,double longitude){
            this.latitude = latitude;
            this.longitude = longitude;
        }
        public LatLng getLatLng(){
            return new LatLng(latitude,longitude);
        }
    }
}
