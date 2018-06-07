package it.unive.dais.cevid.datadroid.lib.progress;

/**
 * Created by spano on 03/01/2018.
 */

public class ProgressCounter {
    protected final int step;
    protected int cnt;

    public ProgressCounter(int start, int step) {
        this.cnt = start;
        this.step = step;
    }

    public ProgressCounter() {
        this(0, 1);
    }

    public void stepCounter() {
        cnt += step;
    }

    public int getCurrentCounter() {
        return cnt;
    }
}
