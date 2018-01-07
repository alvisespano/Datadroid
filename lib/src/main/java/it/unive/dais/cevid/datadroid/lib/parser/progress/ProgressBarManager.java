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

    private static final String TAG = "ProgressBarManager";
    @NonNull
    protected final ProgressBar progressBar;
    @NonNull
    protected final ConcurrentLinkedQueue<Object> owners = new ConcurrentLinkedQueue<>();
    @NonNull
    protected final Activity ctx;

    public ProgressBarManager(@NonNull Activity ctx, @NonNull ProgressBar progressBar) {
        this.progressBar = progressBar;
        this.ctx = ctx;
    }

    @NonNull
    public Handle<ProgressBar> acquire(@NonNull Object owner) {
        synchronized (owners) {
            if (owners.isEmpty()) onFirstAcquire();
            owners.add(owner);
            return new Handle<ProgressBar>() {
                @Override
                public void close() {
                    synchronized (owners) {
                        owners.remove(owner);
                        if (owners.isEmpty()) onLastRelease();
                    }
                }

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
        }
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
