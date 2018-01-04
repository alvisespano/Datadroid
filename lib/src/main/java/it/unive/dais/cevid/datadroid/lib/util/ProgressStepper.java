package it.unive.dais.cevid.datadroid.lib.util;

/**
 * Created by spano on 03/01/2018.
 */

public class ProgressStepper {
    protected int cnt = 0;

    public void step() {
        ++cnt;
    }

    public int getCurrentProgress() {
        return cnt;
    }
}
