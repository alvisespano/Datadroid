package it.unive.dais.cevid.aac.component;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.util.EntitieExpenditure;
import it.unive.dais.cevid.aac.item.MunicipalityItem;
import it.unive.dais.cevid.aac.parser.MunicipalityParser;
import it.unive.dais.cevid.aac.util.AppCompatActivityWithProgressBar;
import it.unive.dais.cevid.aac.util.AsyncTaskWithProgressBar;
import it.unive.dais.cevid.datadroid.lib.parser.AppaltiParser;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;

public class MunicipalitySearchActivity extends AppCompatActivityWithProgressBar {
    public static final String MUNICIPALITY_ITEM = "MUNICIPALITY_ITEM";
    private static final int MAX_SIZE = 100;
    SoldipubbliciParser soldipubbliciParser;
    MunicipalityParser comuniParser;
    AppaltiParser appaltiParser;
    public static String CODICE_ENTE = "ENTE", CODICE_COMPARTO = "COMPARTO";
    MunicipalityItem comune;
    String ente, comparto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_municipality_search);

        ente = getIntent().getStringExtra(CODICE_ENTE);
        comparto = getIntent().getStringExtra(CODICE_COMPARTO);

        setProgressBar();

        comune = (MunicipalityItem) getIntent().getSerializableExtra(MUNICIPALITY_ITEM);

        soldipubbliciParser = new CustomSoldipubbliciParser(comparto, ente);
        ((CustomSoldipubbliciParser) soldipubbliciParser).setCallerActivity(this);
        soldipubbliciParser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        comuniParser = new MunicipalityParser(new InputStreamReader(getResources().openRawResource(
                getResources().getIdentifier("comuni",
                        "raw", getPackageName()))));

        comuniParser.setCallerActivity(this);
        comuniParser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        appaltiParser = new AppaltiParser(comune.getUrls());
        //((CustomAppaltiParser) appaltiParser).setCallerActivity(this);
        appaltiParser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        Button btnBalance = (Button) findViewById(R.id.municipality_balance_button);
        Button btnTender = (Button) findViewById(R.id.municipality_tender_button);

        btnBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBalance();
            }
        });

        btnTender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickTender();
            }
        });

        ((TextView) findViewById(R.id.municipality_title)).setText(comune.getTitle());
        ((TextView) findViewById(R.id.municipality_desc)).setText(comune.getDescription());
    }

    private void clickTender() {
        Intent intent = new Intent(MunicipalitySearchActivity.this, MunicipalityTenderActivity.class);
        try {
            intent.putExtra("appalti_ente", (Serializable) appaltiParser.getAsyncTask().get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


    protected void clickBalance() {
        String descrizione_ente = comune.getDescription(), numero_abitanti = comune.getCapite();
        List spese_ente_2017 = new ArrayList<EntitieExpenditure>();
        List spese_ente_2016 = new ArrayList<EntitieExpenditure>();
        List spese_ente_2015 = new ArrayList<EntitieExpenditure>();
        List spese_ente_2014 = new ArrayList<EntitieExpenditure>();
        List spese_ente_2013 = new ArrayList<EntitieExpenditure>();

        /** codice_comparto = findCodiceCompartoByDescrizioneEnte(descrizione_ente);
         codice_ente = findCodiceEnteByDescrizioneEnte(descrizione_ente);
         numero_abitanti = findNumeroAbitantiByDescrizioneEnte(descrizione_ente);*/

        try {
            List<SoldipubbliciParser.Data> l = new ArrayList<>(soldipubbliciParser.getAsyncTask().get());
            for (SoldipubbliciParser.Data x : l) {
                if (!(x.importo_2017).equals("0") && !(x.importo_2017).equals("null") && !(x.importo_2017).equals("")) {
                    spese_ente_2017.add(new EntitieExpenditure(x, "2017"));
                }
                if (!(x.importo_2016).equals("0") && !(x.importo_2016).equals("null") && !(x.importo_2016).equals("")) {
                    spese_ente_2016.add(new EntitieExpenditure(x, "2016"));
                }
                if (!(x.importo_2015).equals("0") && !(x.importo_2015).equals("null") && !(x.importo_2015).equals("")) {
                    spese_ente_2015.add(new EntitieExpenditure(x, "2015"));
                }
                if (!(x.importo_2014).equals("0") && !(x.importo_2014).equals("null") && !(x.importo_2014).equals("")) {
                    spese_ente_2014.add(new EntitieExpenditure(x, "2014"));
                }
                if (!(x.importo_2013).equals("0") && !(x.importo_2013).equals("null") && !(x.importo_2013).equals("")) {
                    spese_ente_2013.add(new EntitieExpenditure(x, "2013"));
                }
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(MunicipalitySearchActivity.this, MunicipalityResultActivity.class);

        /*//crop size, quick fix for crash
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(spese_ente_2013,new EntiComparator("2013"));
            Collections.sort(spese_ente_2014,new EntiComparator("2014"));
            Collections.sort(spese_ente_2015,new EntiComparator("2015"));
            Collections.sort(spese_ente_2016,new EntiComparator("2016"));
            Collections.sort(spese_ente_2017,new EntiComparator("2017"));
            //non serve, il Comparator è già decrescente.
            Collections.reverse(spese_ente_2013);
            Collections.reverse(spese_ente_2014);
            Collections.reverse(spese_ente_2015);
            Collections.reverse(spese_ente_2016);
            Collections.reverse(spese_ente_2017);

        }
        if(spese_ente_2013.size() > MAX_SIZE)spese_ente_2013.subList(MAX_SIZE,spese_ente_2013.size()).clear();
        if(spese_ente_2014.size() > MAX_SIZE)spese_ente_2014.subList(MAX_SIZE,spese_ente_2014.size()).clear();
        if(spese_ente_2015.size() > MAX_SIZE)spese_ente_2015.subList(MAX_SIZE,spese_ente_2015.size()).clear();
        if(spese_ente_2016.size() > MAX_SIZE)spese_ente_2016.subList(MAX_SIZE,spese_ente_2016.size()).clear();
        if(spese_ente_2017.size() > MAX_SIZE)spese_ente_2017.subList(MAX_SIZE,spese_ente_2017.size()).clear();*/

        intent.putExtra("numero_abitanti", numero_abitanti);
        intent.putExtra("descrizione_ente", descrizione_ente);
        intent.putExtra("spese_ente_2017", (Serializable) spese_ente_2017);
        intent.putExtra("spese_ente_2016", (Serializable) spese_ente_2016);
        intent.putExtra("spese_ente_2015", (Serializable) spese_ente_2015);
        intent.putExtra("spese_ente_2014", (Serializable) spese_ente_2014);
        intent.putExtra("spese_ente_2013", (Serializable) spese_ente_2013);

        startActivity(intent);
    }

    @Override
    public void setProgressBar() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_comuni);
    }

    protected class CustomSoldipubbliciParser extends SoldipubbliciParser implements AsyncTaskWithProgressBar {

        private static final String TAG = "CustomSoldipubbliciParser";
        private AppCompatActivityWithProgressBar caller;

        protected String codiceComparto;
        protected String codiceEnte;

        public CustomSoldipubbliciParser(String codiceComparto, String codiceEnte) {
            super(codiceComparto, codiceEnte);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            caller.requestProgressBar(this);
        }

        @Override
        protected void onPostExecute(@NonNull List<SoldipubbliciParser.Data> r) {
            super.onPostExecute(r);
            caller.releaseProgressBar(this);
        }

        @Override
        public void setCallerActivity(AppCompatActivityWithProgressBar caller) {
            this.caller = caller;
        }
    }

    protected class CustomAppaltiParser extends AppaltiParser implements AsyncTaskWithProgressBar {
        private static final String TAG = "MyAppaltiParser";
        private AppCompatActivityWithProgressBar caller;

        public CustomAppaltiParser(List<URL> urls) {
            super(urls);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            caller.requestProgressBar(this);
        }

        @Override
        protected void onPostExecute(@NonNull List<AppaltiParser.Data> r) {
            super.onPostExecute(r);
            caller.releaseProgressBar(this);
        }

        @Override
        public void setCallerActivity(AppCompatActivityWithProgressBar caller) {
            this.caller = caller;
        }
    }
}

