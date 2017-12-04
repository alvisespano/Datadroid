package it.unive.dais.cevid.aac.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.component.MunicipalitySearchActivity;
import it.unive.dais.cevid.aac.component.SupplierSearchActivity;
import it.unive.dais.cevid.aac.component.UniversitySearchActivity;
import it.unive.dais.cevid.aac.item.MunicipalityItem;
import it.unive.dais.cevid.aac.util.ListItem;
import it.unive.dais.cevid.datadroid.lib.util.MapItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private View rootView;
    private ListView listView;


    public ListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_list, container, false);
        assert parentActivity != null;
        listView =(ListView) rootView.findViewById(R.id.fragment_list_view);
        listView.setOnItemClickListener(this);
        redraw(parentActivity.getCurrentMode());
        return rootView;

    }

    @Override
    public void redraw(Mode mode) {
        assert parentActivity != null;
        switch (mode){
            case SUPPLIER:
                putItems(getDataFromItems(parentActivity.getSupplierItems()));
                break;
            case UNIVERSITY:
                putItems(getDataFromItems(parentActivity.getUniversityItems()));
                break;
            case MUNICIPALITY:
                putItems(getDataFromItems(parentActivity.getMunicipalityItems()));
                break;
            default:
                break;
        }
    }

    private <I extends MapItem> List<ListItem> getDataFromItems(Collection<I> collection) {
        List<ListItem> list = new ArrayList<>();
        for(I item : collection){
            ListItem e = new ListItem(item);
            list.add(e);
        }
        return list;
    }

    public void putItems(@NonNull List<ListItem> items){
        ArrayAdapter<ListItem> adapter = new ArrayAdapter<>(getContext(), R.layout.list_fragment, R.id.list_object,items);
        listView.setAdapter(adapter);

    }

    @Override
    public Type getType() {
        return Type.LIST;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListItem item = (ListItem) parent.getItemAtPosition(position);
        MapItem content = item.getItem();
        Intent intent;
        switch (parentActivity.getCurrentMode()){
            case MUNICIPALITY:
                MunicipalityItem municipalityItem = (MunicipalityItem) content;
                intent = new Intent(getContext(), MunicipalitySearchActivity.class);
                intent.putExtra(MunicipalitySearchActivity.CODICE_ENTE, municipalityItem.getCodiceEnte());
                intent.putExtra(MunicipalitySearchActivity.CODICE_COMPARTO, municipalityItem.getCodiceComparto());
                intent.putExtra(MunicipalitySearchActivity.MUNICIPALITY_ITEM, municipalityItem);
                startActivity(intent);
                break;
            case UNIVERSITY:
                intent = new Intent(getContext(), UniversitySearchActivity.class);
                intent.putExtra(UniversitySearchActivity.UNIVERSITY_ITEM, content);
                startActivity(intent);
                break;
            case SUPPLIER:
                intent = new Intent(getContext(), SupplierSearchActivity.class);
                intent.putExtra(SupplierSearchActivity.SUPPLIER_ITEM, content);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
