package it.unive.dais.cevid.aac.component;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.parser.TenderParser;
import it.unive.dais.cevid.aac.adapter.TenderAdapter;
import it.unive.dais.cevid.aac.parser.ParticipantParser;
import it.unive.dais.cevid.aac.util.AppCompatActivityWithProgressBar;
import it.unive.dais.cevid.aac.util.RecyclerItemClickListener;

public class SupplierResultActivity extends AppCompatActivityWithProgressBar {
    public static final String TAG = "SupplierResultActivity";
    protected static final String BUNDLE_PARTECIPATIONS = "PARTS";
    private List<ParticipantParser.Data> tenders;
    private Map<ParticipantParser.Data, TenderParser.Data> map;
    private List<TenderParser> parsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        map = new HashMap<>();
        parsers = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_result);
        setProgressBar();
        Intent intent = getIntent();
        RecyclerView.LayoutManager lmanager = new LinearLayoutManager(this);
        RecyclerView view = (RecyclerView) findViewById(R.id.list_results);
        view.setLayoutManager(lmanager);

        tenders = (List<ParticipantParser.Data>) intent.getSerializableExtra(BUNDLE_PARTECIPATIONS);
        for (ParticipantParser.Data p : tenders) {
            String lotto = p.id_lotto;
            TenderParser bandiParser = new TenderParser(lotto);
            parsers.add(bandiParser);
            bandiParser.setCallerActivity(this);
            bandiParser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        TenderAdapter adapter = new TenderAdapter(tenders);
        view.setAdapter(adapter);
        view.addOnItemTouchListener(
                new RecyclerItemClickListener(getBaseContext(), view, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        try {
                            List<TenderParser.Data> data = parsers.get(position).getAsyncTask().get();
                            if (!(data == null || data.size() <= 0)) {
                                TenderParser.Data tender = data.get(0);
                                map.put(tenders.get(position), tender);
                                Intent intent = new Intent(SupplierResultActivity.this, SupplierDetailsActivity.class);
                                intent.putExtra(SupplierDetailsActivity.BUNDLE_BID, tender);
                                startActivity(intent);
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }

    @Override
    public void setProgressBar() {
        this.progressBar = (ProgressBar) findViewById(R.id.progress_bar_supplier_result);
    }
}
