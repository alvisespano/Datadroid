package it.unive.dais.cevid.datadroid.lib.sync;

import android.support.annotation.NonNull;

import it.unive.dais.cevid.datadroid.lib.util.Function;

/**
 * Created by spano on 22/12/2017.
 */

public interface Pool<T> {
    @NonNull Handle<T> acquire();
    void release(@NonNull Handle<T> x);
}
