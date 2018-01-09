package it.unive.dais.cevid.datadroid.lib.parser.progress;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import java.util.concurrent.ConcurrentLinkedQueue;

import it.unive.dais.cevid.datadroid.lib.util.Function;

/**
 * Created by spano on 05/01/2018.
 */
public class ProgressBarManager {

<<<<<<< HEAD
    protected static class Descr {
        @NonNull
        public final ProgressBar progressBar;
        @Nullable
        private Object owner;

        public Descr(@NonNull ProgressBar progressBar, @Nullable Object owner) {
            this.progressBar = progressBar;
            this.owner = owner;
        }

        @Override
        public String toString() {
            return String.format("[bar(%s) owner(%s:%s)]", System.identityHashCode(progressBar), System.identityHashCode(getOwner()),
                    getOwner() != null ? getOwner().getClass().getSimpleName() : null);
        }

        public boolean isOwner(@NonNull Object owner) {
            return System.identityHashCode(this.getOwner()) == System.identityHashCode(owner);
        }

        public boolean hasOwner() {
            return getOwner() != null;
        }

        @Nullable
        protected Object getOwner() {
            return owner;
        }

        protected void setOwner(@NonNull Object owner) {
            this.owner = owner;
        }

        protected void cleanOwner() {
            this.owner = null;
        }

    }

=======
>>>>>>> parent of 3f51caf... Multiple progress bar support for ProgressBarManager
    private static final String TAG = "ProgressBarManager";
    @NonNull
    protected final ProgressBar progressBar;
    @NonNull
    protected final ConcurrentLinkedQueue<Object> owners = new ConcurrentLinkedQueue<>();
    @NonNull
    protected final Activity ctx;

    public ProgressBarManager(@NonNull Activity ctx, @NonNull ProgressBar progressBar) {
<<<<<<< HEAD
        this(ctx, new ProgressBar[]{progressBar});
    }


    public ProgressBarManager(Activity ctx, ProgressBar[] progressBars) {
        this(ctx, Arrays.asList(progressBars));
=======
        this.progressBar = progressBar;
        this.ctx = ctx;
>>>>>>> parent of 3f51caf... Multiple progress bar support for ProgressBarManager
    }

    @NonNull
    public Handle<ProgressBar> acquire(@NonNull Object owner) {
<<<<<<< HEAD
        findOwned(owner);   // just try to allocate a descr at first acquire
        return new Handle<ProgressBar>() {
            @Override
            public void close() {
                synchronized (q) {
                    for (Descr d : q) {
                        if (d.isOwner(owner)) {
                            Log.d(TAG, String.format("releasing descr %s", d));
                            d.cleanOwner();
                            onLastRelease(d.progressBar);
                        }
=======
        synchronized (owners) {
            if (owners.isEmpty()) onFirstAcquire();
            owners.add(owner);
            return new Handle<ProgressBar>() {
                @Override
                public void close() {
                    synchronized (owners) {
                        owners.remove(owner);
                        if (owners.isEmpty()) onLastRelease();
>>>>>>> parent of 3f51caf... Multiple progress bar support for ProgressBarManager
                    }
                }
            }

<<<<<<< HEAD
            @Override
            @Nullable
            public <R> R apply(@NonNull Function<ProgressBar, R> f) {
                synchronized (q) {
                    Descr d = findOwned(owner);
                    if (d != null) {
                        return f.apply(d.progressBar);
                    } else return null;
                }
            }
        };

    }

    @Nullable
    private Descr findOwned(@NonNull Object owner) {
        synchronized (q) {
            for (Descr d : q) {
                if (d.isOwner(owner)) {
                    Log.d(TAG, String.format("found descr %s already owned by %s", d, System.identityHashCode(owner)));
                    return d;
                }
            }
            for (Descr d : q) {
                if (!d.hasOwner()) {
                    d.setOwner(owner);
                    onFirstAcquire(d.progressBar);
                    Log.d(TAG, String.format("allocated descr %s", d));
                    return d;
                }
            }
            Log.d(TAG, String.format("cannot allocate bar now (owner %s is waiting)", System.identityHashCode(owner)));
            return null;
=======
                @Override
                @Nullable
                public <R> R apply(@NonNull Function<ProgressBar, R> f) {
                    synchronized (owners) {
                        Object top = owners.peek();
                        if (top != null && owner == top)
                            return f.apply(progressBar);
                        else {
                            Log.d(TAG, String.format("owner(%s) != myself(%s): apply skipped", owner, this));
                            return null;
                        }
                    }
                }
            };
>>>>>>> parent of 3f51caf... Multiple progress bar support for ProgressBarManager
        }
    }

    public void release(@NonNull Handle<ProgressBar> h) {
        h.release();
    }

    protected void onLastRelease(@NonNull ProgressBar progressBar) {
        Log.d(TAG, "parser progressbar: GONE");
        ctx.runOnUiThread(() -> progressBar.setVisibility(View.GONE));
    }

    protected void onFirstAcquire(@NonNull ProgressBar progressBar) {
        Log.d(TAG, "parser progressbar: VISIBLE");
        ctx.runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));
    }

}
