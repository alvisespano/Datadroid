package it.unive.dais.cevid.datadroid.lib.sync;

import android.support.annotation.NonNull;

/**
 * Created by spano on 13/12/17.
 */
public class SingletonPool<T> implements Pool<T> {
    @NonNull
    protected final T content;
    private int cnt = 0;

    public SingletonPool(@NonNull T x) {
        this.content = x;
    }

    @NonNull
    public Handle<T> acquire() {
        synchronized (this) {
            if (++cnt > 0) {
                onFirstAcquire();
            }
            return new Handle<T>(content) {
                @Override
                public void close() {
                    SingletonPool.this.release(this);
                }
            };
        }
    }

    @Override
    public void release(@NonNull Handle<T> x) {
        synchronized (this) {
            if (--cnt <= 0) {
                cnt = 0;
                onLastRelease();
            }
        }
    }

    protected void onFirstAcquire() {}
    protected void onLastRelease() {}

}
