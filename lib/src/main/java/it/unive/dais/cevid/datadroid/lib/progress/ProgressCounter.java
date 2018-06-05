package it.unive.dais.cevid.datadroid.lib.progress;

/**
 * Created by spano on 03/01/2018.
 */

public class ProgressCounter {
    private final int increment;
    protected int cnt = 0;

    public ProgressCounter(int increment) {
        this.increment = increment;
    }

    public ProgressCounter() {
        this(1);
    }

    public void stepCounter() {
        cnt += increment;
    }

    public int getCurrentCounter() {
        return cnt;
    }
}
