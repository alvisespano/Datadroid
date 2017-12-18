package it.unive.dais.cevid.aac.component;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationSettingsStates;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.fragment.BaseFragment;
import it.unive.dais.cevid.aac.fragment.ListFragment;
import it.unive.dais.cevid.aac.item.MunicipalityItem;
import it.unive.dais.cevid.aac.item.SupplierItem;
import it.unive.dais.cevid.aac.item.UniversityItem;
import it.unive.dais.cevid.aac.fragment.MapFragment;
import it.unive.dais.cevid.aac.util.AsyncTaskWithProgressBar;
import it.unive.dais.cevid.datadroid.lib.parser.EntitiesParser;
import it.unive.dais.cevid.aac.parser.SupplierParser;
import it.unive.dais.cevid.aac.util.AppCompatActivityWithProgressBar;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;
import it.unive.dais.cevid.datadroid.lib.util.ProgressStepper;
import it.unive.dais.cevid.datadroid.lib.util.UnexpectedException;

public class MainActivity extends AppCompatActivityWithProgressBar implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    protected static final int REQUEST_CHECK_SETTINGS = 500;
    protected static final int PERMISSIONS_REQUEST_ACCESS_BOTH_LOCATION = 501;
    protected EntitiesParser<?> entitiesParser;

    private @NonNull
    BaseFragment activeFragment = new MapFragment();
    private @Nullable
    BottomNavigationView bottomNavigation;
    private @NonNull
    FragmentManager fragmentManager = getSupportFragmentManager();

    public enum Mode {
        UNIVERSITY,
        MUNICIPALITY,
        SUPPLIER
    }

    @Override
    public void setProgressBar() {
        this.progressBar = (ProgressBar) findViewById(R.id.progress_bar_main);
    }


    @NonNull
    private final Collection<UniversityItem> universityItems = new ConcurrentLinkedQueue<>();
    @NonNull
    private final Collection<MunicipalityItem> municipalityItems = new ConcurrentLinkedQueue<>();
    @NonNull
    private final Collection<SupplierItem> supplierItems = new ConcurrentLinkedQueue<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setProgressBar();
        setContentFragment(R.id.content_frame, activeFragment);

        bottomNavigation = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(this);

        setupSupplierItems();
        setupUniversityItems();
        setupMunicipalityItems();
    }

    private void setContentFragment(int container, @NonNull BaseFragment fragment) {
        fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(container, fragment, fragment.getTag())
                .commit();

    }

    private void changeActiveFragment(@NonNull BaseFragment fragment) {
        this.activeFragment = fragment;
        setContentFragment(R.id.content_frame, fragment);
    }

    //stub for change button onClick listener
    public void onChangeType() {
        switch (activeFragment.getType()) {
            case MAP:
                changeActiveFragment(new ListFragment());
                break;
            case LIST:
                changeActiveFragment(new MapFragment());
                break;
            default:
                break;
        }
    }

    private void setupSupplierItems() {
        SupplierParser supplierParser = new SupplierParser() {
            @Override
            public void onItemParsed(@NonNull SupplierParser.Data x) {
                supplierItems.add(new SupplierItem(MainActivity.this, x));
            }
        };
        supplierParser.setCallerActivity(this);
        supplierParser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setupUniversityItems() {
        //add Ca Foscari
        try {
            List<URL> urls = new ArrayList<>();
            urls.add(new URL("http://www.unive.it/avcp/datiAppalti2016.xml"));
            universityItems.add(new UniversityItem("000704968000000", "Università Ca' Foscari", "Università degli studi di Venezia", "1", 45.437576, 12.3289554, urls));
        } catch (MalformedURLException e) {
            Log.w(TAG, "malformed url");
            e.printStackTrace();
        }

        //add Padova
        try {
            List<URL> urls = new ArrayList<>();
            urls.add(new URL("http://www.unipd.it/sites/unipd.it/files/dataset_2016_s1_01.xml"));
            urls.add(new URL("http://www.unipd.it/sites/unipd.it/files/dataset_2016_s1_02.xml"));
            urls.add(new URL("http://www.unipd.it/sites/unipd.it/files/dataset_2016_s1_03.xml"));
            urls.add(new URL("http://www.unipd.it/sites/unipd.it/files/dataset_2016_s2_01.xml"));
            urls.add(new URL("http://www.unipd.it/sites/unipd.it/files/dataset_2016_s2_02.xml"));
            universityItems.add(new UniversityItem("000058546000000", "Università di Padova", "Università degli studi di Padova", "1", 45.406766, 11.8774462, urls));
        } catch (MalformedURLException e) {
            Log.w(TAG, "malformed url");
            e.printStackTrace();
        }

        //add Trento
        try {
            List<URL> urls = new ArrayList<>();
            urls.add(new URL("http://approvvigionamenti.unitn.it/bandi-di-gara-e-contratti/2017/ricerca_2016.xml"));
            urls.add(new URL("http://approvvigionamenti.unitn.it/bandi-di-gara-e-contratti/2017/amministrazione_2016.xml"));
            universityItems.add(new UniversityItem("000067046000000", "Università di Trento", "Università degli studi di Trento", "1", 46.0694828, 11.1188738, urls));
        } catch (MalformedURLException e) {
            Log.w(TAG, "malformed url");
            e.printStackTrace();
        }
    }

    private void setupMunicipalityItems() {

        entitiesParser = new EntitiesParser();
        entitiesParser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        List<EntitiesParser.Data> entities = new ArrayList();

        try {
            List<EntitiesParser.Data> el = new ArrayList<>(entitiesParser.getAsyncTask().get());
            for (EntitiesParser.Data x : el) {
                if (x.descrizione_ente.equals("COMUNE DI ROMA"))
                    entities.add(x);

            }
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, String.format("exception caught during parser %s", entitiesParser.getName()));
            e.printStackTrace();
        }



        //municipalityItems.add(new MunicipalityItem("Venezia", 45.4375466, 12.3289983, "Comune di Venezia", "000066862"));
        //municipalityItems.add(new MunicipalityItem("Milano", 45.4628327, 9.1075204, "Comune di Milano", "800000013"));
        //municipalityItems.add(new MunicipalityItem("Torino", 45.0735885, 7.6053946, "Comune di Torino", "000098618"));
        //municipalityItems.add(new MunicipalityItem("Bologna", 44.4992191, 11.2614736, "Comune di Bologna", "000250878"));
        //municipalityItems.add(new MunicipalityItem("Genova", 44.4471368, 8.7504034, "Comune di Genova", "000164848"));

        /*try {
            List<URL> urls = new ArrayList<>();
            urls.add(new URL("https://www.comune.fi.it/export/sites/retecivica/01307110484/2016_L190_1.xml"));
            urls.add(new URL("https://www.comune.fi.it/export/sites/retecivica/01307110484/2016_L190_2.xml"));
            municipalityItems.add(new MunicipalityItem("800000038", "Firenze", "Comune di Firenze", 43.7800606, 11.1707559, urls));
        } catch (MalformedURLException e) {
            Log.w(TAG, "malformed url");
            e.printStackTrace();
        }*/

        try {
            EntitiesParser.Data roma = new EntitiesParser.Data();
            for (EntitiesParser.Data x : entities)
                if (x.descrizione_ente.equals("COMUNE DI ROMA"))
                    roma = x;

            List<URL> urls = new ArrayList<>();
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/avcp_m02_2016.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/avcp_m02_2016.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/Bandi_e_contratti_Elenchi_annuali_avcp_m03_2016.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/avcp-m04-2016.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/avcp.m05.2016gen.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/avcp-m06-2016_1.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/avcp-m07-2016.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/avcp-m08-2016.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/AVCP_31dic16.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/avcp-m10-2016.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/Mun_XV_Elenco_Bandi_2016_xml.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/mun_12_avcp_2016.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/avcp-m13-2016_NUOVO.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/avcp-m14-2016.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/avcp-m15-2016.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/elenchi_annuali_avcp-d01-2016.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/avcp-D02-2016.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/avcp-d12-2016.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/avcp-d03-2016-2.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/avcp_Dipartimento_Patrimonio_2016.xml\n"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/xmlnuovo.xml"));
            urls.add(new URL("http://www.urbanistica.comune.roma.it/images/dipartimento/elenchi-annuali-2016.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/avcp-d08-2016_gennaio2017.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/avcp-d09-2016_2.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/avcp2016completo.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/avcpd11_2016.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/avcp-dip.tut.ambient-2016.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/avcp-d32-2016_new.xml"));
            urls.add(new URL("http://www.sovraintendenzaroma.it/content/download/20537/548162/version/5/file/avcp-d14-2016.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/Elenchi_annuali_2016_V1.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/avcp-d16-2016.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/avcp-d18-2016.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/avcp-d19-2016_DIT.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/avcp-ORU-2016.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/avcp-d21-2016.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/Elenco2016_Art1c32_L190_2012_avcp-d17-2016.xml"));
            urls.add(new URL("https://www.comune.roma.it/resources/cms/documents/avcp_d25_2016.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/Elenchi_ANAC_UfficioAssembleaCapitolina_2016.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/avcp-d26-2016.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/elenchi_annuali_DRS_2016.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/File_xml_gare_2016.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/avcp-d28-2016.xml"));
            urls.add(new URL("http://www.comune.roma.it/resources/cms/documents/avcp-d30-2016.xml"));
            municipalityItems.add(new MunicipalityItem(roma.codice_ente, "Roma", roma.descrizione_ente, roma.numero_abitanti ,41.9102411, 12.3955688, urls));
        } catch (MalformedURLException e) {
            Log.w(TAG, "malformed url");
            e.printStackTrace();
        }
        //municipalityItems.add(new MunicipalityItem("Roma", 41.9102411, 12.3955688, "Comune di Roma", "800000047"));
        //municipalityItems.add(new MunicipalityItem("Napoli", 40.854042, 14.1763903, "Comune di Napoli", "000708829"));
        //municipalityItems.add(new MunicipalityItem("Palermo", 38.1406577, 13.2870764, "Comune di Palermo", "800000060"));
        //municipalityItems.add(new MunicipalityItem("Cagliari", 39.2254656, 9.0932726, "Comune di Cagliari", "000021556"))
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "location service connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "location service connection suspended");
        Toast.makeText(this, R.string.conn_suspended, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "location service connection lost");
        Toast.makeText(this, R.string.conn_failed, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_BOTH_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "permissions granted: ACCESS_FINE_LOCATION + ACCESS_COARSE_LOCATION");
                } else {
                    Log.e(TAG, "permissions not granted: ACCESS_FINE_LOCATION + ACCESS_COARSE_LOCATION");
                    Snackbar.make(this.findViewById(R.id.main_view), R.string.msg_permissions_not_granted, Snackbar.LENGTH_LONG);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maps_with_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.menu_info:
                startActivity(new Intent(this, InfoActivity.class));
                break;
            case R.id.menu_button_swap:
                changeItemIcon(item);
                onChangeType();
                break;
        }
        return false;
    }

    private void changeItemIcon(MenuItem item) {
        switch (activeFragment.getType()) {
            case LIST:
                item.setIcon(R.drawable.ic_view_list);
                break;
            case MAP:
                item.setIcon(R.drawable.ic_view_map);
                break;
        }
    }

    /**
     * Quando arriva un Intent viene eseguito questo metodo.
     * Può essere esteso e modificato secondo le necessità.
     *
     * @see Activity#onActivityResult(int, int, Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(intent);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // inserire codice qui
                        break;
                    case Activity.RESULT_CANCELED:
                        // o qui
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Applica le impostazioni (preferenze) della mappa ad ogni chiamata.
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Pulisce la mappa quando l'app viene distrutta.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Mode mode = getModeByMenuItemId(item.getItemId());
        Log.d(TAG, String.format("entering mode %s", mode));
        activeFragment.redraw(mode);
        return true;
    }

    @SuppressLint("DefaultLocale")
    private static Mode getModeByMenuItemId(int id) {
        switch (id) {
            case R.id.menu_university:
                return Mode.UNIVERSITY;
            case R.id.menu_municipality:
                return Mode.MUNICIPALITY;
            case R.id.menu_suppliers:
                return Mode.SUPPLIER;
            default:
                throw new UnexpectedException(String.format("invalid menu item id: %d", id));
        }
    }

    public Mode getCurrentMode() {
        assert bottomNavigation != null;
        return getModeByMenuItemId(bottomNavigation.getSelectedItemId());
    }

    @NonNull
    public Collection<UniversityItem> getUniversityItems() {
        return universityItems;
    }

    @NonNull
    public Collection<SupplierItem> getSupplierItems() {
        return supplierItems;
    }

    @NonNull
    public Collection<MunicipalityItem> getMunicipalityItems() {
        return municipalityItems;
    }


    // test stuff
    //
    //

    private void testProgressStepper() {
        final int n1 = 10, n2 = 30, n3 = 5;
        ProgressStepper p1 = new ProgressStepper(n1);
        for (int i = 0; i < n1; ++i) {
            ProgressStepper p2 = p1.getSubProgressStepper(n2);
            for (int j = 0; j < n2; ++j) {
                p2.step();
                Log.d(TAG, String.format("test progress: %d%%", (int) (p2.getPercent() * 100.)));
            }
            p1.step();
            Log.d(TAG, String.format("test progress: %d%%", (int) (p1.getPercent() * 100.)));
        }
    }
}
