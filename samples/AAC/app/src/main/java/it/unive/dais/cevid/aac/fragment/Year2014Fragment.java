package it.unive.dais.cevid.aac.fragment;

import it.unive.dais.cevid.aac.component.MunicipalityResultActivity;

import java.util.List;

import it.unive.dais.cevid.aac.util.EntitieExpenditure;

/**
 * Created by gianmarcocallegher on 15/11/17.
 */

public class Year2014Fragment extends YearFragment {

    @Override
    protected List<EntitieExpenditure> getSpendingListFromData() {
        return ((MunicipalityResultActivity)getActivity()).getSpese_Ente_2014();
    }
}
