package it.unive.dais.cevid.datadroid.lib.sync;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Created by spano on 20/12/2017.
 */
public class ProgressBarSingletonPool extends SingletonPool<ProgressBar> {
    private final Activity ctx;

    public ProgressBarSingletonPool(@NonNull Activity ctx, @NonNull ProgressBar x) {
        super(x);
        this.ctx = ctx;
    }

    @Override
    protected void onLastRelease() {
        ctx.runOnUiThread(() -> content.setVisibility(View.GONE));
    }

    @Override
    protected void onFirstAcquire() {
        ctx.runOnUiThread(() -> content.setVisibility(View.VISIBLE));
    }
}
