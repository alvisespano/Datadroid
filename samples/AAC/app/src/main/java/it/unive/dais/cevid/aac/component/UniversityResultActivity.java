package it.unive.dais.cevid.aac.component;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.adapter.AppaltiAdapter;
import it.unive.dais.cevid.aac.adapter.SoldiPubbliciAdapter;
import it.unive.dais.cevid.aac.util.EntitieExpenditure;
import it.unive.dais.cevid.datadroid.lib.parser.AppaltiParser;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;
import it.unive.dais.cevid.datadroid.lib.util.DataManipulation;
import it.unive.dais.cevid.datadroid.lib.util.Function;


public class UniversityResultActivity extends AppCompatActivity {
    private static final String TAG = "UniversityResultActivity";

    public static final String LIST_APPALTI = "LIST_APPALTI";
    public static final String LIST_SOLDIPUBBLICI = "LIST_SOLDIPUBBLICI";
//    private static final String MODE = "MODE";

    private enum Mode {
        APPALTI,
        SOLDI_PUBBLICI,
        COMBINE,
        UNKNOWN;

        public static Mode ofIntent(Intent i) {
            if (i.hasExtra(LIST_APPALTI) && i.hasExtra(LIST_SOLDIPUBBLICI)) return COMBINE;
            if (i.hasExtra(LIST_APPALTI)) return APPALTI;
            if (i.hasExtra(LIST_SOLDIPUBBLICI)) return SOLDI_PUBBLICI;
            return UNKNOWN; //throw new UnexpectedException("Unknown intent labels would lead to unsupported mode");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_university_result);
        Intent i = getIntent();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        switch (Mode.ofIntent(i)) {
            case APPALTI: {
                RecyclerView v = (RecyclerView) findViewById(R.id.list_tenders);
                v.setLayoutManager(layoutManager);
                Serializable l0 = i.getSerializableExtra(LIST_APPALTI);
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
                break;
            }

            case SOLDI_PUBBLICI: {
                RecyclerView v = (RecyclerView) findViewById(R.id.list_exp);
                v.setLayoutManager(layoutManager);
                Serializable l0 = i.getSerializableExtra(LIST_SOLDIPUBBLICI);
                List<SoldipubbliciParser.Data> l = (List<SoldipubbliciParser.Data>) l0;
                List el = new ArrayList<EntitieExpenditure>();

                for (SoldipubbliciParser.Data x : l)
                    el.add(new EntitieExpenditure(x, "2016"));

                SoldiPubbliciAdapter soldiPubbliciAdapter = new SoldiPubbliciAdapter(el, "1");
                v.setAdapter(soldiPubbliciAdapter);
                break;
            }

            case COMBINE:{
                RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(this);

                RecyclerView v1 = (RecyclerView) findViewById(R.id.list_exp);
                RecyclerView v2 = (RecyclerView) findViewById(R.id.list_tenders);
                v1.setLayoutManager(layoutManager);
                v2.setLayoutManager(layoutManager2);

                Serializable l0 = i.getSerializableExtra(LIST_SOLDIPUBBLICI);
                Serializable l1 = i.getSerializableExtra(LIST_APPALTI);
                List<SoldipubbliciParser.Data> l2 = (List<SoldipubbliciParser.Data>) l0;
                List<AppaltiParser.Data> l3 = (List<AppaltiParser.Data>) l1;

                List el = new ArrayList<EntitieExpenditure>();

                for (SoldipubbliciParser.Data x : l2)
                    el.add(new EntitieExpenditure(x, "2016"));

                SoldiPubbliciAdapter soldiPubbliciAdapter = new SoldiPubbliciAdapter(el, "1");
                v1.setAdapter(soldiPubbliciAdapter);
                v1.setVisibility(View.VISIBLE);

                AppaltiAdapter appaltiAdapter = new AppaltiAdapter(l3);
                v2.setAdapter(appaltiAdapter);
                v2.setVisibility(View.VISIBLE);

                LinearLayout lo = (LinearLayout) findViewById(R.id.sum_tenders);
                lo.setVisibility(View.VISIBLE);
                TextView tv = (TextView) findViewById(R.id.sum_exp);
                Double sum = DataManipulation.sumBy(l3, new Function<AppaltiParser.Data, Double>() {
                    @Override
                    public Double apply(AppaltiParser.Data x) {
                        return Double.valueOf(x.importo);
                    }
                });
                tv.setText(String.valueOf(sum));
                break;
            }
            
            default: {
                Log.e("URA", "unknown mode");
            }

        }

    }


}

