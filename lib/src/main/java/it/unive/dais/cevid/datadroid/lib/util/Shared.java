package it.unive.dais.cevid.datadroid.lib.util;

import android.support.annotation.NonNull;

/**
 * Created by spano on 13/12/17.
 */
public class Shared<T> {
    @NonNull
    protected final T x;
    private int cnt = 0;

    public Shared(@NonNull T x) {
        this.x = x;
    }

    @NonNull
    public Handle acquire() {
        synchronized (this) {
            if (++cnt > 0) {
                onActualAcquire();
            }
            return new Handle(x) {
                @Override
                public void close() {
                    synchronized (this) {
                        if (--cnt <= 0) {
                            cnt = 0;
                            onActualRelease();
                        }
                    }
                }
            };
        }
    }

    protected void onActualAcquire() {}
    protected void onActualRelease() {}

    /**
     * Created by spano on 19/12/2017.
     */
    public abstract class Handle implements AutoCloseable {

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
                x = f.apply(x);
            }
        }

    }
}
