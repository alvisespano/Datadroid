package it.unive.dais.cevid.aac.component;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.item.MunicipalityItem;
import it.unive.dais.cevid.aac.parser.MunicipalityParser;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;
import it.unive.dais.cevid.datadroid.lib.util.ProgressStepper;

public class MunicipalitySearchActivity extends AppCompatActivity {
    public static final String COMUNE = "COMUNE";
    private static final int MAX_SIZE = 100;
    SoldipubbliciParser soldipubbliciParser;
    MunicipalityParser comuniParser;
    public static String CODENTE = "ENTE", CODCOMPARTO = "COMPARTO";
    MunicipalityItem comune;
    String ente;
    String comparto;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_municipality_search);
        ente = getIntent().getStringExtra(CODENTE);
        comparto = getIntent().getStringExtra(CODCOMPARTO);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_comuni);
        comune = (MunicipalityItem) getIntent().getSerializableExtra(COMUNE);
        soldipubbliciParser = new CustomSoldiParser(comparto, ente);
        soldipubbliciParser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        comuniParser = new MunicipalityParser(comune.getTitle());
        comuniParser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        Button btn = (Button) findViewById(R.id.municipality_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click();
            }
        });
        ((TextView) findViewById(R.id.comuni_title)).setText(comune.getTitle());
        ((TextView) findViewById(R.id.com_desc)).setText(comune.getDescription());
    }


    protected void click() {
        String numero_abitanti = "0", descrizione_ente = comune.getDescription();
        List spese_ente_2017 = new ArrayList<SoldipubbliciParser.Data>();
        List spese_ente_2016 = new ArrayList<SoldipubbliciParser.Data>();
        List spese_ente_2015 = new ArrayList<SoldipubbliciParser.Data>();
        List spese_ente_2014 = new ArrayList<SoldipubbliciParser.Data>();
        List spese_ente_2013 = new ArrayList<SoldipubbliciParser.Data>();

        /** codice_comparto = findCodiceCompartoByDescrizioneEnte(descrizione_ente);
         codice_ente = findCodiceEnteByDescrizioneEnte(descrizione_ente);
         numero_abitanti = findNumeroAbitantiByDescrizioneEnte(descrizione_ente);*/

        try {
            List<SoldipubbliciParser.Data> l = new ArrayList<>(soldipubbliciParser.getAsyncTask().get());
            List<MunicipalityParser.Data> c = new ArrayList<>(comuniParser.getAsyncTask().get());
            numero_abitanti = (c.isEmpty()) ? "0" : c.get(0).popolazione_residente;
            for (SoldipubbliciParser.Data x : l) {
                if (!(x.importo_2017).equals("0") && !(x.importo_2017).equals("null") && !(x.importo_2017).equals("")) {
                    spese_ente_2017.add(x);
                }
                if (!(x.importo_2016).equals("0") && !(x.importo_2016).equals("null") && !(x.importo_2016).equals("")) {
                    spese_ente_2016.add(x);
                }
                if (!(x.importo_2015).equals("0") && !(x.importo_2015).equals("null") && !(x.importo_2015).equals("")) {
                    spese_ente_2015.add(x);
                }
                if (!(x.importo_2014).equals("0") && !(x.importo_2014).equals("null") && !(x.importo_2014).equals("")) {
                    spese_ente_2014.add(x);
                }
                if (!(x.importo_2013).equals("0") && !(x.importo_2013).equals("null") && !(x.importo_2013).equals("")) {
                    spese_ente_2013.add(x);
                }
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(MunicipalitySearchActivity.this, MunicipalityResultActivity.class);
/*
        //crop size, quick fix for crash
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
        if(spese_ente_2017.size() > MAX_SIZE)spese_ente_2017.subList(MAX_SIZE,spese_ente_2017.size()).clear();
*/
        intent.putExtra("numero_abitanti", numero_abitanti);
        intent.putExtra("descrizione_ente", descrizione_ente);
        intent.putExtra("spese_ente_2017", (Serializable) spese_ente_2017);
        intent.putExtra("spese_ente_2016", (Serializable) spese_ente_2016);
        intent.putExtra("spese_ente_2015", (Serializable) spese_ente_2015);
        intent.putExtra("spese_ente_2014", (Serializable) spese_ente_2014);
        intent.putExtra("spese_ente_2013", (Serializable) spese_ente_2013);

        startActivity(intent);
    }

    protected class CustomSoldiParser extends SoldipubbliciParser {

        private static final String TAG = "CustomSoldipubbliciParser";

        protected String codiceComparto;
        protected String codiceEnte;

        public CustomSoldiParser(String codiceComparto, String codiceEnte) {
            super(codiceComparto, codiceEnte);
            progressBar.setIndeterminate(false);
            progressBar.setMax(100);
        }

        @Override
        public void onProgressUpdate(ProgressStepper p) {
            super.onProgressUpdate(p);
            int progress = (int) (p.getPercent() * progressBar.getMax());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                progressBar.setProgress(progress, true);
            } else {
                progressBar.setProgress(progress);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<Data> r) {
            super.onPostExecute(r);
            progressBar.setVisibility(View.GONE);
        }
    }
}
