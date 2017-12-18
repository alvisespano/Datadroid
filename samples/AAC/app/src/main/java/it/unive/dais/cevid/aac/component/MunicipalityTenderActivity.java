package it.unive.dais.cevid.aac.component;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.adapter.AppaltiAdapter;
import it.unive.dais.cevid.datadroid.lib.parser.AppaltiParser;
import it.unive.dais.cevid.datadroid.lib.util.DataManipulation;
import it.unive.dais.cevid.datadroid.lib.util.Function;

public class MunicipalityTenderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_municipality_tender);
        Intent i = getIntent();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        RecyclerView v = (RecyclerView) findViewById(R.id.list_tenders);
        v.setLayoutManager(layoutManager);
        Serializable l0 = i.getSerializableExtra("appalti_ente");
        List<AppaltiParser.Data> l = (List<AppaltiParser.Data>) l0;
        AppaltiAdapter ad = new AppaltiAdapter(l);
        v.setAdapter(ad);

        LinearLayout lo = (LinearLayout) findViewById(R.id.sum_tenders);
        lo.setVisibility(View.VISIBLE);
        TextView tv = (TextView) findViewById(R.id.sum_exp);
        Double sum = DataManipulation.sumBy(l, new Function<AppaltiParser.Data, Double>() {
            @Override
            public Double apply(AppaltiParser.Data x) {
                return Double.valueOf(x.importo);
            }
        });
        tv.setText(String.valueOf(sum));
    }
}
