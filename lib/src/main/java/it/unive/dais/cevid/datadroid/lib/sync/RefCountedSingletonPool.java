package it.unive.dais.cevid.datadroid.lib.sync;

import android.support.annotation.NonNull;

/**
 * Created by spano on 13/12/17.
 */
public class RefCountedSingletonPool<T> implements Pool<T> {
    @NonNull
    protected final T x;
    private int cnt = 0;

    public RefCountedSingletonPool(@NonNull T x) {
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
                    RefCountedSingletonPool.this.release(x);
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
