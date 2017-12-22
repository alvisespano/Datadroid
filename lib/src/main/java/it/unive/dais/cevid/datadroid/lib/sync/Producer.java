package it.unive.dais.cevid.datadroid.lib.sync;

import android.support.annotation.NonNull;

/**
 * Created by spano on 22/12/2017.
 */

public interface Producer<T> {
    @NonNull Handle<T> acquire();
    void release(@NonNull T x);
}
