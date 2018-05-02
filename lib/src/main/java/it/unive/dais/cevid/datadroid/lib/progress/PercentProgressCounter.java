package it.unive.dais.cevid.datadroid.lib.progress;

import android.util.Log;

import it.unive.dais.cevid.datadroid.lib.util.UnexpectedException;

/**
 * Created by spano on 30/10/2017.
 */
public class PercentProgressCounter extends ProgressCounter {

    private static final String TAG = "PercentProgressCounter";
    private final int size;
    private final float base, scale;

    public PercentProgressCounter(int size) {
        this(size, 0.f, 1.f);
    }

    public PercentProgressCounter(int size, float base, float scale) {
        this.size = size;
        this.base = base;
        this.scale = scale;
    }

    public PercentProgressCounter subsection(int newSize) {
        return new PercentProgressCounter(newSize, getPercent(), 1.f / (float) size);
    }

    public float getPercent() {
        float p = (float) cnt / (float) size;
        return base + p * scale;
    }

    @Override
    public int getCurrentCounter() {
        float p = getPercent();
        if (p < 0.f || p > 1.f)
            throw new UnexpectedException(String.format("ProgressCounter.getPercent() return %f", p));
        @SuppressWarnings("MagicNumber")
        int r = Math.round(p * 100.f);
        Log.d(TAG, String.format("getCurrentCounter(): %d", r));
        return r;
    }

}

