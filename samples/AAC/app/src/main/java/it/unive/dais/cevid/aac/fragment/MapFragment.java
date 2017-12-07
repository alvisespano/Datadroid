package it.unive.dais.cevid.aac.fragment;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Collection;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.component.MainActivity;
import it.unive.dais.cevid.aac.component.MunicipalitySearchActivity;
import it.unive.dais.cevid.aac.component.SettingsActivity;
import it.unive.dais.cevid.aac.component.SupplierSearchActivity;
import it.unive.dais.cevid.aac.component.UniversitySearchActivity;
import it.unive.dais.cevid.aac.item.MunicipalityItem;
import it.unive.dais.cevid.aac.item.SupplierItem;
import it.unive.dais.cevid.aac.item.UniversityItem;
import it.unive.dais.cevid.datadroid.lib.util.MapItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends BaseFragment
        implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnMarkerClickListener,
        BottomNavigationView.OnNavigationItemSelectedListener {


    private static final String TAG = "MapFragment";
    /**
     * Questo oggetto è la mappa di Google Maps. Viene inizializzato asincronamente dal metodo {@code onMapsReady}.
     */
    @Nullable
    protected GoogleMap gMap;
    /**
     * Pulsanti in sovraimpressione gestiti da questa app. Da non confondere con i pulsanti che GoogleMaps mette in sovraimpressione e che non
     * fanno parte degli oggetti gestiti manualmente dal codice.
     */
    protected ImageButton button_here, button_car;
    /**
     * Il marker che viene creato premendo il pulsante button_here (cioè quello dell'app, non quello di Google Maps).
     * E' utile avere un campo d'istanza che tiene il puntatore a questo marker perché così è possibile rimuoverlo se necessario.
     * E' null quando non è stato creato il marker, cioè prima che venga premuto il pulsante HERE la prima volta.
     */
    @Nullable
    protected Marker hereMarker = null;


//    public void setParentActivity(@NonNull MainActivity activity) {
//        this.parentActivity = activity;
//    }

    /**
     * Metodo proprietario che imposta la visibilità del pulsante HERE.
     * Si occupa di nascondere o mostrare il pulsante HERE in base allo zoom attuale, confrontandolo con la soglia di zoom
     * impostanta nelle preferenze.
     * Questo comportamento è dimostrativo e non è necessario tenerlo quando si sviluppa un'applicazione modificando questo template.
     */
    public void setHereButtonVisibility() {
        if (gMap != null) {
            if (gMap.getCameraPosition().zoom < SettingsActivity.getZoomThreshold(getContext())) {
                button_here.setVisibility(View.INVISIBLE);
            } else {
                button_here.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_map, container, false);
        MapView mapView = (MapView) mView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.getMapAsync(this);

        button_here = (ImageButton) mView.findViewById(R.id.button_here);
        button_car = (ImageButton) mView.findViewById(R.id.button_car);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        button_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "here button clicked");
                gpsCheck();
                updateCurrentPosition();
                if (hereMarker != null) hereMarker.remove();
                if (currentPosition != null) {
                    MarkerOptions opts = new MarkerOptions();
                    opts.position(currentPosition);
                    opts.title(getString(R.string.marker_title));
                    opts.snippet(String.format("lat: %g\nlng: %g", currentPosition.latitude, currentPosition.longitude));
                    assert gMap != null;
                    hereMarker = gMap.addMarker(opts);
                    if (gMap != null)
                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, getResources().getInteger(R.integer.zoomFactor_button_here)));
                } else
                    Log.d(TAG, "no current position available");
            }
        });
        return mView;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.gMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_BOTH_LOCATION);
        } else {
            gMap.setMyLocationEnabled(true);
        }
        UiSettings uis = gMap.getUiSettings();
        uis.setZoomGesturesEnabled(true);
        uis.setMyLocationButtonEnabled(true);
        applyMapSettings();
        gMap.setOnMyLocationButtonClickListener(
                new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        gpsCheck();
                        return false;
                    }
                });
        gMap.setOnMapClickListener(this);
        gMap.setOnMapLongClickListener(this);
        gMap.setOnCameraMoveStartedListener(this);
        gMap.setOnMarkerClickListener(this);
        uis.setCompassEnabled(true);
        uis.setZoomControlsEnabled(true);
        uis.setMapToolbarEnabled(true);
        assert parentActivity != null;
        redraw(parentActivity.getCurrentMode());

        gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                final MapItem markerTag = (MapItem) marker.getTag();
                if (markerTag instanceof UniversityItem) {
                    if (hereMarker == null || (hereMarker.getPosition() != marker.getPosition())) {
                        Intent intent = new Intent(getContext(), UniversitySearchActivity.class);
                        intent.putExtra(UniversitySearchActivity.UNIVERSITY_ITEM, markerTag);
                        startActivity(intent);
                    }
                } else if (markerTag instanceof MunicipalityItem) {
                    MunicipalityItem item = (MunicipalityItem) markerTag;
                    Intent intent = new Intent(getContext(), MunicipalitySearchActivity.class);
                    intent.putExtra(MunicipalitySearchActivity.CODICE_ENTE, item.getId());
                    intent.putExtra(MunicipalitySearchActivity.CODICE_COMPARTO, item.getCodiceComparto());
                    intent.putExtra(MunicipalitySearchActivity.MUNICIPALITY_ITEM, item);
                    startActivity(intent);
                } else if (markerTag instanceof SupplierItem) {
                    Intent intent = new Intent(getContext(), SupplierSearchActivity.class);
                    intent.putExtra(SupplierSearchActivity.SUPPLIER_ITEM, markerTag);
                    startActivity(intent);
                }
            }
        });
        LatLng rome = new LatLng(41.89, 12.51);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rome, 5));
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onCameraMoveStarted(int i) {
        setHereButtonVisibility();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        marker.showInfoWindow();
        button_car.setVisibility(View.VISIBLE);
        button_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, R.string.msg_button_car, Snackbar.LENGTH_SHORT);
                if (currentPosition != null) {
                    navigate(currentPosition, marker.getPosition());
                }
            }
        });
        return false;
    }

    /**
     * Viene chiamato quando si clicca sulla mappa.
     * Aggiungere qui codice che si vuole eseguire quando l'utente clicca sulla mappa.
     *
     * @param latLng la posizione del click.
     */
    @Override
    public void onMapClick(LatLng latLng) {
        // nascondi il pulsante della navigazione (non quello di google maps, ma il nostro pulsante custom)
        button_car.setVisibility(View.INVISIBLE);
    }

    /**
     * Naviga dalla posizione from alla posizione to chiamando il navigatore di Google.
     *
     * @param from posizione iniziale.
     * @param to   posizione finale.
     */
    protected void navigate(@NonNull LatLng from, @NonNull LatLng to) {
        Intent navigation = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=" + from.latitude + "," + from.longitude + "&daddr=" + to.latitude + "," + to.longitude + ""));
        navigation.setPackage("com.google.android.apps.maps");
        Log.i(TAG, String.format("starting navigation from %s to %s", from, to));
        startActivity(navigation);
    }


    protected void applyMapSettings() {
        if (gMap != null) {
            Log.d(TAG, "applying map settings");
            gMap.setMapType(SettingsActivity.getMapStyle(getContext()));
        }
        setHereButtonVisibility();
    }

    @Override
    public void onResume() {
        super.onResume();
        applyMapSettings();
    }

    @Override
    public void onPause() {
        super.onPause();
//        NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
//        int id = getContext().getResources().getInteger(R.integer.id_notification);
//        assert mNotificationManager != null;
//        mNotificationManager.cancel(id);
    }

    /**
     * Pulisce la mappa quando l'app viene distrutta.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (gMap != null) gMap.clear();
    }

    @Override
    public void redraw(MainActivity.Mode mode) {
        if (gMap != null) {
            gMap.clear();
            assert parentActivity != null;
            switch (mode) {
                case MUNICIPALITY:
                    putMarkers(parentActivity.getMunicipalityItems(), BitmapDescriptorFactory.HUE_GREEN);
                    break;
                case UNIVERSITY:
                    putMarkers(parentActivity.getUniversityItems(), BitmapDescriptorFactory.HUE_RED);
                    break;
                case SUPPLIER:
                    putMarkers(parentActivity.getSupplierItems(), BitmapDescriptorFactory.HUE_BLUE);
                    break;
            }
        }
    }

    @Override
    public Type getType() {
        return Type.MAP;
    }

    public <I extends MapItem> void putMarkers(@NonNull Collection<I> c, float hue) {
        for (I i : c) {
            putMarker(hue, i);
        }
    }

    public <I extends MapItem> void putMarker(float hue, I i) {
        MarkerOptions opts = new MarkerOptions()
                .position(i.getPosition())
                .title(i.getTitle())
                .snippet(i.getDescription())
                .icon(BitmapDescriptorFactory.defaultMarker(hue));
        assert gMap != null;
        Marker marker = gMap.addMarker(opts);
        marker.setTag(i);
    }

}
