package it.unive.dais.cevid.datadroid.lib.parser.progress;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

import it.unive.dais.cevid.datadroid.lib.util.Function;

/**
 * Created by spano on 05/01/2018.
 */
public class ProgressBarManager {

    protected static class Descr {
        @NonNull
        public final ProgressBar progressBar;
        @Nullable
        public Object owner;

        public Descr(@NonNull ProgressBar progressBar, @Nullable Object owner) {
            this.progressBar = progressBar;
            this.owner = owner;
        }

        @Override
        public String toString() {
            return String.format("[bar(%s) owner(%s)]", System.identityHashCode(progressBar), System.identityHashCode(owner));
        }
    }

    private static final String TAG = "ProgressBarManager";
    @NonNull
    protected final ConcurrentLinkedQueue<Descr> q;
    @NonNull
    protected final Activity ctx;

    public ProgressBarManager(@NonNull Activity ctx, @NonNull Collection<ProgressBar> progressBars) {
        this.ctx = ctx;
        this.q = new ConcurrentLinkedQueue<>();
        for (ProgressBar p : progressBars)
            this.q.add(new Descr(p, null));
    }

    public ProgressBarManager(@NonNull Activity ctx, @NonNull ProgressBar progressBar) {
        this(ctx, new ProgressBar[] { progressBar });
    }


    public ProgressBarManager(Activity ctx, ProgressBar[] progressBars) {
        this(ctx, Arrays.asList(progressBars));
    }

    @NonNull
    public Handle<ProgressBar> acquire(@NonNull Object owner) {
        synchronized (q) {
            Descr d = findOwned(owner);
//            if (owners.isEmpty()) onFirstAcquire();
//            owners.add(owner);
            return new Handle<ProgressBar>() {
                @Override
                public void close() {
                    synchronized (q) {
                        for (Descr d : q) {
                            if (d.owner == owner) {
                                Log.d(TAG, String.format("releasing descr %s", d));
                                d.owner = null;
                            }
                        }
                    }
                }

                @Override
                @Nullable
                public <R> R apply(@NonNull Function<ProgressBar, R> f) {
                    synchronized (q) {
                        Descr d = findOwned(owner);
                        if (d != null) {
                            return f.apply(d.progressBar);
                        }
                        else return null;
                    }
                }
            };
        }
    }

    @Nullable
    private Descr findOwned(Object owner) {
        for (Descr d : q) {
            if (d.owner == owner) {
                Log.d(TAG, String.format("found descr %s already owned by %s", d, System.identityHashCode(owner)));
                return d;
            }
        }
        return allocateUnused(owner);
    }

    @Nullable
    private Descr allocateUnused(Object owner) {
        for (Descr d : q) {
            if (d.owner == null) {
                d.owner = owner;
                Log.d(TAG, String.format("allocated descr %s", d));
                return d;
            }
        }
        Log.d(TAG, String.format("cannot allocate bar now (owner %s is waiting)", System.identityHashCode(owner)));
        return null;
    }

    public void release(@NonNull Handle<ProgressBar> h) {
        h.release();
    }

    protected void onLastRelease() {
        Log.d(TAG, "parser progressbar: GONE");
        ctx.runOnUiThread(() -> progressBar.setVisibility(View.GONE));
    }

    protected void onFirstAcquire() {
        Log.d(TAG, "parser progressbar: VISIBLE");
        ctx.runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));
    }

}
