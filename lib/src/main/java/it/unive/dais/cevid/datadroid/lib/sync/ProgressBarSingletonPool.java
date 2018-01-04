package it.unive.dais.cevid.datadroid.lib.sync;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Created by spano on 20/12/2017.
 */
public class ProgressBarSingletonPool extends SingletonPool<ProgressBar> {
    public ProgressBarSingletonPool(@NonNull ProgressBar x) {
        super(x);
    }

    @Override
    protected void onLastRelease() {
//        content.setVisibility(View.GONE);
        Log.d("BAR", "gone");
    }

    @Override
    protected void onFirstAcquire() {
//        content.setVisibility(View.VISIBLE);
        Log.d("BAR", "visible");
    }
}
