package it.unive.dais.cevid.datadroid.lib.sync;

import android.support.annotation.NonNull;

/**
 * Created by spano on 13/12/17.
 */
public class RefCounter<T> implements Producer<T> {
    @NonNull
    protected final T x;
    private int cnt = 0;

    public RefCounter(@NonNull T x) {
        this.x = x;
    }

    @NonNull
    public Handle<T> acquire() {
        synchronized (this) {
            if (++cnt > 0) {
                onActualAcquire();
            }
            return new Handle<T>(x) {
                @Override
                public void close() {
                    RefCounter.this.release(x);
                }
            };
        }
    }

    @Override
    public void release(@NonNull T x) {
        synchronized (this) {
            if (--cnt <= 0) {
                cnt = 0;
                onActualRelease();
            }
        }
    }

    protected void onActualAcquire() {}
    protected void onActualRelease() {}


}
