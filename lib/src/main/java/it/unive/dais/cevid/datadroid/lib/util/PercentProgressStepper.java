package it.unive.dais.cevid.datadroid.lib.util;

/**
 * Created by spano on 30/10/2017.
 */
public class PercentProgressStepper implements PercentProgress {

    private final int size;
    private int cnt = 0;
    private final double base, scale;

    public PercentProgressStepper(int size) {
        this(size, 0.0, 1.0);
    }

    public PercentProgressStepper(int size, double base, double scale) {
        this.size = size;
        this.base = base;
        this.scale = scale;
    }

    public PercentProgressStepper getSubProgressStepper(int newSize) {
        return new PercentProgressStepper(newSize, getPercent(), 1.0 / (double) size);
    }

    public void step() {
        ++cnt;
    }

    public double getPercent() {
        double p = (double) cnt / (double) size;
        return base + p * scale;
    }

    public int getPercent100() {
        return (int) (getPercent() * 100.);
    }
}

