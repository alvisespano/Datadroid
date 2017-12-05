package it.unive.dais.cevid.aac.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.List;

import it.unive.dais.cevid.aac.R;
import it.unive.dais.cevid.aac.component.MunicipalityResultActivity;
import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;

/**
 * Created by gianmarcocallegher on 15/11/17.
 */

public class Year2017Fragment extends YearFragment {

    @Override
    protected double getSpendingItemFromData(SoldipubbliciParser.Data data) throws NumberFormatException {
        return Double.parseDouble(data.importo_2017);
    }
    @Override
    protected List<SoldipubbliciParser.Data> getSpendingListFromData() {
        return ((MunicipalityResultActivity)getActivity()).getSpese_Ente_2017();
    }
}
