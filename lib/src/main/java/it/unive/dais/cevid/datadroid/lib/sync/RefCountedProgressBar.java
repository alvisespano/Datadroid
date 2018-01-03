package it.unive.dais.cevid.datadroid.lib.sync;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Created by spano on 20/12/2017.
 */
public class RefCountedProgressBar extends RefCountedSingletonPool<ProgressBar> {
    public RefCountedProgressBar(@NonNull ProgressBar x) {
        super(x);
    }

    @Override
    protected void onActualRelease() {
        this.x.setVisibility(View.GONE);
    }

    @Override
    protected void onActualAcquire() {
        this.x.setVisibility(View.VISIBLE);
    }
}
