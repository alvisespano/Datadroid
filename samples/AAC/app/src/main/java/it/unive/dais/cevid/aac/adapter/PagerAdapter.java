package it.unive.dais.cevid.aac.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
;import it.unive.dais.cevid.aac.fragment.Year2013Fragment;
import it.unive.dais.cevid.aac.fragment.Year2014Fragment;
import it.unive.dais.cevid.aac.fragment.Year2015Fragment;
import it.unive.dais.cevid.aac.fragment.Year2016Fragment;
import it.unive.dais.cevid.aac.fragment.Year2017Fragment;

/**
 * Created by gianmarcocallegher on 15/11/17.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new Year2017Fragment();
            case 1:
                return new Year2016Fragment();
            case 2:
                return new Year2015Fragment();
            case 3:
                return new Year2014Fragment();
            case 4:
                return new Year2013Fragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}