package it.unive.dais.cevid.datadroid.lib.progress;

import it.unive.dais.cevid.datadroid.lib.util.Prelude;

/**
 * Created by spano on 03/01/2018.
 */
public class ProgressCounter {
    protected final int stop, start;
    protected int cnt;

    public ProgressCounter(int start, int stop) {
        this.start = start;
        this.cnt = start;
        this.stop = stop;
    }

    public ProgressCounter(int size) {
        this(0, size);
    }

    public ProgressCounter subsection(int size) {
        ProgressCounter parent = this;
        return new ProgressCounter(size) {
            @Override
            public double getPercent() {
                return parent.getPercent() + super.getPercent() * parent.size();
            }
        };
    }

    public int size() {
        return stop - start;
    }

    public void step() {
        cnt = Prelude.crop(start, stop, ++cnt);
    }

    public int getCurrentCounter() {
        return cnt;
    }

    public double getPercent() {
        return ((double) (cnt - start)) / size();
    }
}
