package it.unive.dais.cevid.datadroid.lib.sync;

import android.support.annotation.NonNull;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by spano on 22/12/2017.
 */

public class ConcurrentPool<T> implements Pool<T> {
    @NonNull
    protected final ConcurrentLinkedQueue<T> q = new ConcurrentLinkedQueue<>();

    public ConcurrentPool() {}
    public ConcurrentPool(@NonNull T x) {
        q.add(x);
    }

    @Override
    @NonNull
    public Handle<T> acquire() {
        return new Handle<T>(q.poll()) {
            @Override
            public void close() throws Exception {
                ConcurrentPool.this.release(this);
            }
        };
    }

    public void release(@NonNull Handle<T> h) {
        q.add(h.get());
    }

}
