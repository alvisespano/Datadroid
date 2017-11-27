package it.unive.dais.cevid.aac.fragment;


import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
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

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
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
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;
import java.util.Map;

import it.unive.dais.cevid.aac.component.MunicipalitySearchActivity;
import it.unive.dais.cevid.aac.component.MainActivity;
import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.component.SupplierSearchActivity;
import it.unive.dais.cevid.aac.component.UniversitySearchActivity;
import it.unive.dais.cevid.aac.component.SettingsActivity;
import it.unive.dais.cevid.aac.item.MunicipalityItem;
import it.unive.dais.cevid.aac.item.SupplierItem;
import it.unive.dais.cevid.aac.item.UniversityItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment
        implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnMarkerClickListener,
        BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MapFragment";
    protected static final int REQUEST_CHECK_SETTINGS = 500;
    protected static final int PERMISSIONS_REQUEST_ACCESS_BOTH_LOCATION = 501;

    private MainActivity parentActivity;

//    private List<SupplierItem> suppliers;
//    private List<MunicipalityItem> municipalities;
//    private List<UniversityItem> universities;

    private Map<String, UniversityItem> universityMap = new HashMap<>();
    private Map<String, MunicipalityItem> municipalityMap = new HashMap<>();
    private Map<String, SupplierItem> supplierMap = new HashMap<>();

    /**
     * Questo oggetto è la mappa di Google Maps. Viene inizializzato asincronamente dal metodo {@code onMapsReady}.
     */
    protected GoogleMap gMap;
    /**
     * Pulsanti in sovraimpressione gestiti da questa app. Da non confondere con i pulsanti che GoogleMaps mette in sovraimpressione e che non
     * fanno parte degli oggetti gestiti manualmente dal codice.
     */
    protected ImageButton button_here, button_car;
    /**
     * API per i servizi di localizzazione.
     */
    protected FusedLocationProviderClient fusedLocationClient;
    /**
     * Posizione corrente. Potrebbe essere null prima di essere calcolata la prima volta.
     */
    @Nullable
    protected LatLng currentPosition = null;
    /**
     * Il marker che viene creato premendo il pulsante button_here (cioè quello dell'app, non quello di Google Maps).
     * E' utile avere un campo d'istanza che tiene il puntatore a questo marker perché così è possibile rimuoverlo se necessario.
     * E' null quando non è stato creato il marker, cioè prima che venga premuto il pulsante HERE la prima volta.
     */
    @Nullable
    protected Marker hereMarker = null;

    //public MapFragment() {}

    public void setParentActivity(MainActivity activity) {
        this.parentActivity = activity;
    }

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

        // trova gli oggetti che rappresentano i bottoni e li salva come campi d'istanza
        button_here = (ImageButton) mView.findViewById(R.id.button_here);
        button_car = (ImageButton) mView.findViewById(R.id.button_car);

        // API per i servizi di localizzazione
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
    public void onMapReady(GoogleMap googleMap) {
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
        populateMap(parentActivity.getActiveItem());

        gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                final String id = marker.getId();
                if (universityMap.containsKey(id)) {
                    if (hereMarker == null || (hereMarker.getPosition() != marker.getPosition())) {
                        Intent intent = new Intent(getContext(), UniversitySearchActivity.class);
                        intent.putExtra(UniversitySearchActivity.UNIVERSITY_ITEM, universityMap.get(id));
                        startActivity(intent);
                    }
                } else if (municipalityMap.containsKey(id)) {
                    Intent intent = new Intent(getContext(), MunicipalitySearchActivity.class);
                    MunicipalityItem item = municipalityMap.get(id);
                    intent.putExtra(MunicipalitySearchActivity.CODICE_ENTE, item.getCodiceEnte());
                    intent.putExtra(MunicipalitySearchActivity.CODICE_COMPARTO, item.getCodiceComparto());
                    intent.putExtra(MunicipalitySearchActivity.MUNICIPALITY_ITEM, item);
                    startActivity(intent);
                } else if (supplierMap.containsKey(id)) {
                    Intent intent = new Intent(getContext(), SupplierSearchActivity.class);
                    intent.putExtra(SupplierSearchActivity.SUPPLIER_ITEM, supplierMap.get(id));
                    startActivity(intent);
                }
            }
        });
        LatLng rome = new LatLng(41.89, 12.51);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rome, 5));
    }

    protected void gpsCheck() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(getContext()).addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
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

    public void updateCurrentPosition() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "requiring permission");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_BOTH_LOCATION);
        } else {
            Log.d(TAG, "permission granted");
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location loc) {
                            if (loc != null) {
                                currentPosition = new LatLng(loc.getLatitude(), loc.getLongitude());
                                Log.i(TAG, "current position updated");
                            }
                        }
                    });
        }
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
        NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        int id = getContext().getResources().getInteger(R.integer.id_notification);
        mNotificationManager.cancel(id);
    }

    /**
     * Pulisce la mappa quando l'app viene distrutta.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        gMap.clear();
    }

//    public void setUniversityItems(List<UniversityItem> uni) {
//        this.universities = uni;
//    }
//
//    public void setSupplierItems(List<SupplierItem> supply) {
//        this.suppliers = supply;
//    }
//
//    public void setMunicipalityItems(List<MunicipalityItem> mun) {
//        this.municipalities = mun;
//    }

    protected void populateMap(MenuItem item) {
        if (gMap != null) {
            gMap.clear();
            municipalityMap.clear();
            universityMap.clear();
            supplierMap.clear();
            String title = String.valueOf(item.getTitle());
            if (title.equals(getString(R.string.bottom_menu_university))) {
                putUniversityItems();
            } else if (title.equals(getString(R.string.bottom_menu_public))) {
                putMunicipalityItems();
            } else if (title.equals(getString(R.string.bottom_menu_winners))) {
                putSupplierItems();
            }
        }
    }

    // TODO: questi 3 metodi fanno schifo, sono replicatissimi. Scriverne 1 solo, oppure addirittura usare MapItem.putMarkersFromMapItems()
    private void putSupplierItems() {
        for (SupplierItem f : this.suppliers) {
            MarkerOptions opts = new MarkerOptions()
                    .position(f.getPosition())
                    .title(f.getTitle())
                    .snippet(f.getDescription())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            Marker marker = gMap.addMarker(opts);
            supplierMap.put(marker.getId(), f);
        }
    }

    private void putMunicipalityItems() {
        for (MunicipalityItem c : this.municipalities) {
            MarkerOptions opts = new MarkerOptions()
                    .position(c.getPosition())
                    .title(c.getTitle())
                    .snippet(c.getDescription())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            Marker marker = gMap.addMarker(opts);
            municipalityMap.put(marker.getId(), c);
        }
    }

    private void putUniversityItems() {
        for (UniversityItem u : universities) {
            MarkerOptions opts = new MarkerOptions()
                    .position(u.getPosition())
                    .title(u.getTitle())
                    .snippet(u.getDescription())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            Marker marker = gMap.addMarker(opts);
            universityMap.put(marker.getId(), u);
        }
    }

    public void changeMenuSelection(MenuItem item) {
        this.populateMap(item);
    }

}
