package it.unive.dais.cevid.datadroid.lib.component;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
import it.unive.dais.cevid.datadroid.lib.util.Function;
import it.unive.dais.cevid.datadroid.lib.util.Prelude;

public abstract class MapManager {

    private static final String TAG = "MapManager";

    @NonNull
    @UiThread
    protected abstract GoogleMap getGoogleMap();

    @NonNull
    @UiThread
    protected Marker putMarkerFromMapItem(@NonNull MapItem i, float hue) throws Exception {
        MarkerOptions opts = new MarkerOptions().title(i.getTitle()).position(i.getPosition()).snippet(i.getDescription()).icon(BitmapDescriptorFactory.defaultMarker(hue));
        return getGoogleMap().addMarker(opts);
    }

    /**
     * Metodo di utilità che permette di posizionare rapidamente sulla mappa una lista di MapItem.
     * Attenzione: l'oggetto gMap deve essere inizializzato, questo metodo va pertanto chiamato preferibilmente dalla
     * callback onMapReady().
     *
     * @param l   la lista di oggetti di tipo I tale che I sia sottotipo di MapItem.
     * @param <I> sottotipo di MapItem.
     * @return ritorna la collection di oggetti Marker aggiunti alla mappa.
     */
    @NonNull
    @UiThread
    protected <I extends MapItem> Collection<Marker> putMarkersFromMapItems(@NonNull List<I> l, float hue) {
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

    @Nullable
    protected <I extends MapItem, P extends ProgressCounter> Collection<Marker> putMarkersFromParser(@NonNull AsyncParser<I, P> parser, float hue) {
        try {
            List<I> l = parser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
            return putMarkersFromMapItems(l, hue);
        } catch (Exception e) {
            Log.e(TAG, String.format("exception caught during parser %s: %s", parser.getName(), e));
            e.printStackTrace();
            return null;
        }
    }

    @NonNull
    protected <I extends MapItem> Collection<Marker> putMarkersFromCsv(@NonNull CsvParser parser, @NonNull Function<CsvParser.Row, I> createMapItem, float hue) throws ExecutionException, InterruptedException {
        List<CsvParser.Row> rows = parser.getAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
        List<I> l = new ArrayList<>();
        for (CsvParser.Row r : rows)
            l.add(createMapItem.apply(r));
        return putMarkersFromMapItems(l, hue);
    }

    @NonNull
    public <I extends MapItem> Collection<Marker> putMarkersFromCsv(@NonNull Reader rd, boolean hasHeader, @NonNull String sep, @NonNull Function<CsvParser.Row, I> createMapItem, float hue, @NonNull ProgressBarManager pbm) throws ExecutionException, InterruptedException {
        return putMarkersFromCsv(new CsvParser(rd, hasHeader, sep, pbm), createMapItem, hue);
    }

    @NonNull
    @SuppressLint("StaticFieldLeak")
    public <I extends MapItem> Collection<Marker> putMarkersFromCsv(@NonNull URL url, boolean hasHeader, @NonNull String sep, @NonNull Function<CsvParser.Row, I> createMapItem, float hue, @NonNull ProgressBarManager pbm) throws ExecutionException, InterruptedException {
        return (new AsyncTask<Void, Void, Collection<Marker>>() {
            @Override
            @Nullable
            protected Collection<Marker> doInBackground(Void... params) {
                try {
                    Log.v(TAG, String.format("downloading CSV from %s...", url));
                    return putMarkersFromCsv(Prelude.urlToReader(url), hasHeader, sep, createMapItem, hue, pbm);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
    }

}
