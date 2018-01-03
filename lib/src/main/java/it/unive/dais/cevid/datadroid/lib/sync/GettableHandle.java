package it.unive.dais.cevid.datadroid.lib.sync;

import android.support.annotation.NonNull;

/**
 * Created by spano on 22/12/2017.
 */
public abstract class GettableHandle<T> extends Handle<T> {
    public GettableHandle(@NonNull T x) {
        super(x);
    }

    @NonNull
    public T get() {
        return x;
    }
}
