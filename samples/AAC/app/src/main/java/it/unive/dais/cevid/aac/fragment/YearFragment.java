package it.unive.dais.cevid.aac.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.adapter.SoldiPubbliciAdapter;
import it.unive.dais.cevid.aac.component.MunicipalityDetailsActivity;
import it.unive.dais.cevid.aac.component.MunicipalityResultActivity;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class YearFragment extends Fragment {
    @Nullable
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MunicipalityResultActivity activity = (MunicipalityResultActivity) getActivity();
        String inhabitants = activity.getNumero_abitanti();
        final List<SoldipubbliciParser.Data> spendings = getSpendingListFromData();
        List<String> perCapitaSpendings = new ArrayList<>();
        View rootView = inflater.inflate(R.layout.fragment_year, container, false);

        RecyclerView v = (RecyclerView) rootView.findViewById(R.id.list_exp);

        v.setLayoutManager(new LinearLayoutManager(activity));

        SoldiPubbliciAdapter soldiPubbliciAdapter = new SoldiPubbliciAdapter(spendings);

        v.setAdapter(soldiPubbliciAdapter);

        /*ListView listView = (ListView) rootView.findViewById(R.id.year_list_view);
        EditText inputSearch = (EditText) rootView.findViewById(R.id.search_input);

        for (SoldipubbliciParser.Data x : spendings) {
            double spendingItem;
            try {
                spendingItem = getSpendingItemFromData(x);
            } catch (NumberFormatException ex) {
                spendingItem = 0;
            }
            perCapitaSpendings.add(x.descrizione_codice + "\n" +
                    "Spesa Totale: " + (spendingItem / 100) + "\n" +
                    "Spesa PRO-Capite: " + (spendingItem / 100) / Double.parseDouble(inhabitants)
            );
        }

        adapter = new ArrayAdapter<>(getContext(), R.layout.list_expenditures, R.id.expenditure, perCapitaSpendings);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SoldipubbliciParser.Data data = spendings.get(position);
                Intent intent = new Intent(getContext(), MunicipalityDetailsActivity.class);
                intent.putExtra(MunicipalityDetailsActivity.DATA, data);
                startActivity(intent);
            }
        });

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        return rootView;
    }

    protected abstract double getSpendingItemFromData(SoldipubbliciParser.Data x) throws NumberFormatException;
    protected abstract List<SoldipubbliciParser.Data> getSpendingListFromData();


}
