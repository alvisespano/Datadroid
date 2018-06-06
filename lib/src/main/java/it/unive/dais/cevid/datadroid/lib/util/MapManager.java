package it.unive.dais.cevid.datadroid.lib.util;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.unive.dais.cevid.datadroid.lib.parser.AsyncParser;
import it.unive.dais.cevid.datadroid.lib.parser.CsvParser;
import it.unive.dais.cevid.datadroid.lib.progress.ProgressBarManager;
import it.unive.dais.cevid.datadroid.lib.progress.ProgressCounter;
import it.unive.dais.cevid.datadroid.lib.util.AsyncTaskResult;
import it.unive.dais.cevid.datadroid.lib.util.Function;
import it.unive.dais.cevid.datadroid.lib.util.MapItem;
import it.unive.dais.cevid.datadroid.lib.util.Prelude;

public abstract class MapManager {

    private static final String TAG = "MapManager";

    @NonNull
    @UiThread
    public abstract GoogleMap getGoogleMap();

    @NonNull
    @UiThread
    public Marker putMarkerFromMapItem(@NonNull MapItem i, float hue) throws Exception {
        MarkerOptions opts = new MarkerOptions().title(i.getTitle()).position(i.getPosition()).snippet(i.getDescription()).icon(BitmapDescriptorFactory.defaultMarker(hue));
        Marker m = getGoogleMap().addMarker(opts);
        m.setTag(i);
        return m;
    }

    @NonNull
    @UiThread
    public <I extends MapItem> Collection<Marker> putMarkersFromMapItems(@NonNull List<I> l, float hue) {
        Collection<Marker> r = new ArrayList<>();
        int cnt = 0;
        for (MapItem i : l) {
            try {
                r.add(putMarkerFromMapItem(i, hue));
            } catch (Exception e) {
                Log.w(TAG, String.format("skipping MapItem at position %d: %s", cnt, e.getMessage()));
            }
            ++cnt;
        }
        Log.v(TAG, String.format("added %d markers", cnt));
        return r;
    }

    @NonNull
    @UiThread
    public <I extends MapItem> Collection<Marker> putMarkersFromCsv(@NonNull List<CsvParser.Row> rows, @NonNull Function<CsvParser.Row, I> createMapItem, float hue) {
        List<I> l = new ArrayList<>();
        for (CsvParser.Row r : rows)
            l.add(createMapItem.apply(r));
        return putMarkersFromMapItems(l, hue);
    }

    // TODO: questa api ad alto livello non è comoda come potrebbe sembrare, perché non parallelizza il parsing
    @NonNull
    public <I extends MapItem> Collection<Marker> putMarkersFromCsv(@NonNull Reader rd, boolean hasHeader, @NonNull String sep, @NonNull Function<CsvParser.Row, I> createMapItem, float hue, @Nullable ProgressBarManager pbm) throws ExecutionException, InterruptedException {
        return putMarkersFromCsv(new CsvParser(rd, hasHeader, sep, pbm).getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get(), createMapItem, hue);
    }

    @NonNull
    public <I extends MapItem> Collection<Marker> putMarkersFromCsv(@NonNull URL url, boolean hasHeader, @NonNull String sep, @NonNull Function<CsvParser.Row, I> createMapItem, float hue, @Nullable ProgressBarManager pbm) throws IOException, ExecutionException, InterruptedException {
        Log.v(TAG, String.format("downloading CSV from %s...", url));
        return putMarkersFromCsv(Prelude.urlToReader(url), hasHeader, sep, createMapItem, hue, pbm);
    }

}
