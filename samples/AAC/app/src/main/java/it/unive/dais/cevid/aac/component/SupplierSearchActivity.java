package it.unive.dais.cevid.aac.component;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.item.SupplierItem;
import it.unive.dais.cevid.aac.parser.ParticipantParser;
import it.unive.dais.cevid.aac.util.AppCompatActivityWithProgressBar;

public class SupplierSearchActivity extends AppCompatActivityWithProgressBar {
    public static final String TAG = "SupplierSearchActivity";
    public static String SUPPLIER_ITEM = "SUPPLY";
    private SupplierItem supplier;
    private View mainView;
    private ParticipantParser parser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_search);
        // bundle restore
        if (savedInstanceState == null) {
            // crea l'activity da zero
            supplier = (SupplierItem) getIntent().getSerializableExtra(SUPPLIER_ITEM);
        } else {
            // ricrea l'activity deserializzando alcuni dati dal bundle
            supplier = (SupplierItem) savedInstanceState.getSerializable(SUPPLIER_ITEM);
        }
        //create activity


        this.mainView = findViewById(R.id.supply_info_activity);
        setProgressBar();
        parser = new ParticipantParser(supplier.getPiva());
        parser.setCallerActivity(this);
        parser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        TextView titleView = (TextView) findViewById(R.id.supply_title);
        titleView.setText(supplier.getTitle());

        TextView ivaView = (TextView) findViewById(R.id.supply_vat);
        ivaView.setText(supplier.getPiva());

        TextView addressView = (TextView) findViewById(R.id.supply_address);
        addressView.setText(supplier.getAddress());

        TextView typeView = (TextView) findViewById(R.id.supply_type);
        typeView.setText(supplier.getType());
        Button button = (Button) findViewById(R.id.button_supply_expand);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    List<ParticipantParser.Data> data = parser.getAsyncTask().get();
                    if (data.size() > 0) {
                        Intent intent = new Intent(SupplierSearchActivity.this, SupplierResultActivity.class);
                        intent.putExtra(SupplierResultActivity.BUNDLE_PARTECIPATIONS, new ArrayList<>(data));
                        startActivity(intent);
                    } else {
                        alert(String.format("Trovati %d bandi attivati nel 2016 per %s", data.size(), supplier.getTitle()));
                    }
                } catch (InterruptedException | ExecutionException e) {
                    alert(String.format("Errore inatteso: %s. Riprovare.", e.getMessage()));
                    Log.e(TAG, String.format("exception caught during parser %s", parser.getName()));
                    e.printStackTrace();
                }
            }
        });
    }

    private void alert(String msg) {
        Snackbar.make(mainView, msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void setProgressBar() {
        this.progressBar = (ProgressBar) findViewById(R.id.progress_bar_supplier_search);
    }
}
