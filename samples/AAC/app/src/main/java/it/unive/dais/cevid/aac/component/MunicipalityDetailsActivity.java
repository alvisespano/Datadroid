package it.unive.dais.cevid.aac.component;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;

public class MunicipalityDetailsActivity extends AppCompatActivity {

    public static final String DATA ="DATA" ;
    public static final String PROCAPITE ="PROCAPITE" ;
    private SoldipubbliciParser.Data item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_municipality_details);
        String procapite = getIntent().getStringExtra(PROCAPITE);
        this.item = (SoldipubbliciParser.Data) getIntent().getSerializableExtra(DATA);
        ((TextView)findViewById(R.id.municipality_details_title)).setText(this.item.descrizione_codice);
        ((TextView)findViewById(R.id.municipality_details_siope)).setText(this.item.codice_siope);
        ((TextView)findViewById(R.id.municipality_details_code)).setText(this.item.cod_ente);
        ((TextView)findViewById(R.id.municipality_details_validita)).setText(this.item.data_di_fine_validita);

    }
}
