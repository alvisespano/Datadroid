package it.unive.dais.cevid.aac.component;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.item.UniversityItem;
import it.unive.dais.cevid.aac.parser.ParticipantParser;
import it.unive.dais.cevid.aac.util.AppCompatActivityWithProgressBar;
import it.unive.dais.cevid.aac.util.AsyncTaskWithProgressBar;
import it.unive.dais.cevid.datadroid.lib.parser.AppaltiParser;
import it.unive.dais.cevid.datadroid.lib.parser.AsyncParser;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;
import it.unive.dais.cevid.datadroid.lib.util.DataManipulation;
import it.unive.dais.cevid.datadroid.lib.util.Function;
import it.unive.dais.cevid.datadroid.lib.util.ProgressStepper;

public class UniversitySearchActivity extends AppCompatActivityWithProgressBar {
    private static final String TAG = "UniSearchActivity";

    public static final String UNIVERSITY_ITEM = "UNI";
    private static final String BUNDLE_LIST = "LIST";

    private UniversityItem university;
    private SoldipubbliciParser soldiPubbliciParser;
    private AppaltiParser appaltiParser;
    private LinearLayout mainView;
    private String soldiPubbliciText = " ";
    private String appaltiText = " ";

    @Override
    public void setProgressBar() {
        this.progressBar = (ProgressBar) findViewById(R.id.progress_bar1);
    }

    public class MyActivity extends AppCompatActivityWithProgressBar {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setProgressBar();
        }

        @Override
        public void setProgressBar() {
            this.progressBar = (ProgressBar) findViewById(R.id.progress_bar1);
        }
    }


    // simple progress management for both parsers
    //

    protected class MyAppaltiParser extends AppaltiParser implements AsyncTaskWithProgressBar {
        private static final String TAG = "MyAppaltiParser";
        private AppCompatActivityWithProgressBar caller;

        public MyAppaltiParser(List<URL> urls) {
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

    protected class MySoldipubbliciParser extends SoldipubbliciParser implements AsyncTaskWithProgressBar {
        private static final String TAG = "MySoldipubbliciParser";
        private AppCompatActivityWithProgressBar caller;
        public MySoldipubbliciParser(String a, String b) {
            super(a, b);
        }

        @Override
        public void setCallerActivity(AppCompatActivityWithProgressBar caller) {
            this.caller = caller;
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
    }



    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(UNIVERSITY_ITEM, university);
//        saveParserState(savedInstanceState, appaltiParser);
//        saveParserState(savedInstanceState, soldiPubbliciParser);
        super.onSaveInstanceState(savedInstanceState);
    }

    // TODO: finire questo
    private <T> void saveParserState(Bundle savedInstanceState, AsyncParser<T, ?> parser) {
        try {
            AsyncTask<Void, ?, List<T>> p = parser.getAsyncTask();
            switch (p.getStatus()) {
                case FINISHED:
                    savedInstanceState.putSerializable(BUNDLE_LIST, new ArrayList<T>(p.get()));
                    break;
                default:soldiPubbliciParser:
                    break;
            }
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, String.format("parser %s failed", parser.getClass().getSimpleName()));
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_university_search);
        mainView = (LinearLayout) findViewById(R.id.search_activity);
        setProgressBar();

        if (savedInstanceState == null) {
            // crea l'activity da zero
            university = (UniversityItem) getIntent().getSerializableExtra(UNIVERSITY_ITEM);
        } else {
            // ricrea l'activity deserializzando alcuni dati dal bundle
            university = (UniversityItem) savedInstanceState.getSerializable(UNIVERSITY_ITEM);
        }
        TextView title = (TextView) findViewById(R.id.univeristy_name);
        title.setText(university.getTitle());

        // TODO: salvare lo stato dei parser con un proxy serializzabile
        soldiPubbliciParser = new MySoldipubbliciParser(university.getCodiceComparto(), university.getId());
        appaltiParser = new MyAppaltiParser(university.getUrls());
        ((MySoldipubbliciParser)soldiPubbliciParser).setCallerActivity(this);
        ((MyAppaltiParser)appaltiParser).setCallerActivity(this);
        soldiPubbliciParser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        appaltiParser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        SearchView appaltiSearch = (SearchView) findViewById(R.id.search_tenders);
        SearchView soldipubbliciSearch = (SearchView) findViewById(R.id.search_exp);
        appaltiSearch.onActionViewExpanded();
        soldipubbliciSearch.onActionViewExpanded();

        setSingleListener(appaltiSearch, appaltiParser, UniversityResultActivity.LIST_APPALTI, Appalti_getText, Appalti_getCode, new Function<String, Void>() {
            @Override
            public Void apply(String x) {
                UniversitySearchActivity.this.appaltiText = x;
                return null;
            }
        });
        setSingleListener(soldipubbliciSearch, soldiPubbliciParser, UniversityResultActivity.LIST_SOLDIPUBBLICI, Soldipubblici_getText, Soldipubblici_getCode, new Function<String, Void>() {
            @Override
            public Void apply(String x) {
                UniversitySearchActivity.this.soldiPubbliciText = x;
                return null;
            }
        });

        Button combineButton = (Button) findViewById(R.id.button_combine_data);
        combineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UniversitySearchActivity.this, UniversityResultActivity.class);
                boolean r1 = processQuery(appaltiParser, appaltiText, intent, UniversityResultActivity.LIST_APPALTI, Appalti_getText, Appalti_getCode),
                        r2 = processQuery(soldiPubbliciParser, soldiPubbliciText, intent, UniversityResultActivity.LIST_SOLDIPUBBLICI, Soldipubblici_getText, Soldipubblici_getCode);
                if (r1 && r2)
                    startActivity(intent);
                else
                    alert("Compilare entrambi i campi di testo ed assicurarsi che diano risultati per richiedere una ricerca combinata.");
            }
        });

        mainView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

    }

    private void alert(String msg) {
        Snackbar.make(mainView, msg, Snackbar.LENGTH_SHORT).show();
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private <T> boolean processQuery(AsyncParser<T, ?> parser, String text, Intent intent, String label,
                                     Function<T, String> getText, Function<T, Integer> getCode) {
        try {
            List<T> l = new ArrayList<>(parser.getAsyncTask().get());   // clona la lista per poterla manipolare in sicurezza
            if (!text.isEmpty()) {
                if (text.matches("[0-9]+"))
                    DataManipulation.filterByCode(l, Integer.parseInt(text), getCode);
                else
                    DataManipulation.filterByWords(l, text.split(" "), getText, false);
                if (l.size() == 0) {
                    return false;
                } else {
                    intent.putExtra(label, (Serializable) l);
                    return true;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            alert(String.format("Errore inatteso: %s. Riprovare.", e.getMessage()));
            Log.e(TAG, String.format("exception caught during parser %s", parser.getName()));
            e.printStackTrace();
        }
        return false;
    }

    private <T> void setSingleListener(final SearchView v, final AsyncParser<T, ?> parser, final String label,
                                       final Function<T, String> getText, final Function<T, Integer> getCode, final Function<String, Void> setText) {
        v.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                Intent intent = new Intent(UniversitySearchActivity.this, UniversityResultActivity.class);
                if (processQuery(parser, text, intent, label, getText, getCode))
                    startActivity(intent);
                else
                    alert("La ricerca non ha prodotto nessun risultato. Provare con altri valori.");
                v.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setText.apply(newText);
                return false;
            }
        });
    }

    // higher-order functions
    //
    //

    private static final Function<AppaltiParser.Data, String> Appalti_getText = new Function<AppaltiParser.Data, String>() {
        @Override
        public String apply(AppaltiParser.Data x) {
            return x.oggetto;
        }
    };

    private static final Function<AppaltiParser.Data, Integer> Appalti_getCode = new Function<AppaltiParser.Data, Integer>() {
        @Override
        public Integer apply(AppaltiParser.Data x) {
            return Integer.parseInt(x.cig);
        }
    };

    private static final Function<SoldipubbliciParser.Data, String> Soldipubblici_getText = new Function<SoldipubbliciParser.Data, String>() {
        @Override
        public String apply(SoldipubbliciParser.Data x) {
            return x.descrizione_codice;
        }
    };

    private static final Function<SoldipubbliciParser.Data, Integer> Soldipubblici_getCode = new Function<SoldipubbliciParser.Data, Integer>() {
        @Override
        public Integer apply(SoldipubbliciParser.Data x) {
            return Integer.parseInt(x.codice_siope);
        }
    };

}
