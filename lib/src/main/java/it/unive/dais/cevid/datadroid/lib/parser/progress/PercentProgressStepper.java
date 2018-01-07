package it.unive.dais.cevid.datadroid.lib.parser.progress;

import it.unive.dais.cevid.datadroid.lib.util.UnexpectedException;

/**
 * Created by spano on 30/10/2017.
 */
public class PercentProgressStepper extends ProgressStepper {

    private final int size;
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

    public double getPercent() {
        double p = (double) cnt / (double) size;
        return base + p * scale;
    }

    public int getPercent100() {
        double p = getPercent();
        if (p < 0. || p > 1.)
            throw new UnexpectedException(String.format("ProgressStepper.getPercent() return %d", p));
        return (int) (p * 100.);
    }

}

