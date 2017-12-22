package it.unive.dais.cevid.datadroid.lib.sync;

import android.support.annotation.NonNull;

import it.unive.dais.cevid.datadroid.lib.util.Function;

/**
 * Created by spano on 19/12/2017.
 */
public abstract class Handle<T> implements AutoCloseable {

    @NonNull
    protected T x;

    public Handle(@NonNull T x) {
        this.x = x;
    }

    @Override
    public void finalize() {
        release();
    }

    public void release() {
        try {
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void close() throws Exception;

    public <R> R apply(@NonNull Function<T, R> f) {
        synchronized (this) {
            return f.apply(x);
        }
    }

    public void modify(@NonNull Function<T, T> f) {
        synchronized (this) {
            x = apply(f);
        }
    }

}
