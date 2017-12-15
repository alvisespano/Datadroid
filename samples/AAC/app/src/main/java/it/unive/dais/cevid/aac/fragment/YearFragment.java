package it.unive.dais.cevid.aac.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.adapter.SoldiPubbliciAdapter;
import it.unive.dais.cevid.aac.component.MunicipalityResultActivity;
import it.unive.dais.cevid.aac.util.EntitieExpenditure;

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
        final List<EntitieExpenditure> spendings = getSpendingListFromData();
        View rootView = inflater.inflate(R.layout.fragment_year, container, false);
        RecyclerView v = (RecyclerView) rootView.findViewById(R.id.list_exp);

        v.setLayoutManager(new LinearLayoutManager(activity));

        SoldiPubbliciAdapter soldiPubbliciAdapter = new SoldiPubbliciAdapter(spendings, activity.getNumero_abitanti());

        v.setAdapter(soldiPubbliciAdapter);

        return rootView;
    }

    //protected abstract double getSpendingItemFromData(EntitieExpenditure x) throws NumberFormatException;
    protected abstract List<EntitieExpenditure> getSpendingListFromData();


}
