package it.unive.dais.cevid.datadroid.lib.util;

/**
 * Created by spano on 03/01/2018.
 */

public interface PercentProgress {
    double getPercent();

    default int getPercent100() {
        return (int) (getPercent() * 100.);
    }
}
