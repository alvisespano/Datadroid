package it.unive.dais.cevid.datadroid.lib.util;

/**
 * Created by spano on 03/01/2018.
 */

public interface PercentProgress {
    double getPercent();

    default int getPercent100() {
        double p = getPercent();
        if (p < 0. || p > 1.)
            throw new UnexpectedException(String.format("PercentProgress.getPercent() return %d", p));
        return (int) (p * 100.);
    }
}
