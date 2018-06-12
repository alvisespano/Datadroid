package it.unive.dais.cevid.datadroid.lib.util;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.unive.dais.cevid.datadroid.lib.parser.CsvParser;
import it.unive.dais.cevid.datadroid.lib.progress.ProgressBarManager;

@SuppressWarnings("unused")
public abstract class MapManager {

    private static final String TAG = "MapManager";

    @NonNull
    @UiThread
    public abstract GoogleMap getGoogleMap();

    @NonNull
    @UiThread
    public Marker putMarkerFromMapItem(@NonNull MapItem i, @NonNull Function<MarkerOptions, MarkerOptions> optf) throws Exception {
        //icon(BitmapDescriptorFactory.defaultMarker(hue)
        MarkerOptions opts = optf.apply(new MarkerOptions().title(i.getTitle()).position(i.getPosition()).snippet(i.getDescription()));
        Marker m = getGoogleMap().addMarker(opts);
        m.setTag(i);
        return m;
    }

    @NonNull
    @UiThread
    public <I extends MapItem> List<Marker> putMarkersFromMapItems(@NonNull List<I> l, @NonNull Function<MarkerOptions, MarkerOptions> optf) {
        List<Marker> r = new ArrayList<>();
        int cnt = 0;
        for (MapItem i : l) {
            try {
                r.add(putMarkerFromMapItem(i, optf));
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
    public <I extends MapItem> List<Marker> putMarkersFromCsv(@NonNull List<CsvParser.Row> rows, @NonNull Function<CsvParser.Row, I> createMapItem, @NonNull Function<MarkerOptions, MarkerOptions> optf) {
        List<I> l = new ArrayList<>();
        for (CsvParser.Row r : rows)
            l.add(createMapItem.apply(r));
        return putMarkersFromMapItems(l, optf);
    }

    // TODO: questa api ad alto livello non è comoda come potrebbe sembrare, perché non parallelizza il parsing
    @NonNull
    public <I extends MapItem> List<Marker> putMarkersFromCsv(@NonNull Reader rd, boolean hasHeader, @NonNull String sep, @NonNull Function<CsvParser.Row, I> createMapItem, @NonNull Function<MarkerOptions, MarkerOptions> optf, @Nullable ProgressBarManager pbm) throws ExecutionException, InterruptedException {
        return putMarkersFromCsv(new CsvParser(rd, hasHeader, sep, pbm).getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get(), createMapItem, optf);
    }

    @NonNull
    public <I extends MapItem> List<Marker> putMarkersFromCsv(@NonNull URL url, boolean hasHeader, @NonNull String sep, @NonNull Function<CsvParser.Row, I> createMapItem, @NonNull Function<MarkerOptions, MarkerOptions> optf, @Nullable ProgressBarManager pbm) throws IOException, ExecutionException, InterruptedException {
        Log.v(TAG, String.format("downloading CSV from %s...", url));
        return putMarkersFromCsv(Prelude.urlToReader(url), hasHeader, sep, createMapItem, optf, pbm);
    }

}
