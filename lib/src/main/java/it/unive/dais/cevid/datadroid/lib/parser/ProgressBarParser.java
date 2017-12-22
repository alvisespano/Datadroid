package it.unive.dais.cevid.datadroid.lib.parser;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.List;

import it.unive.dais.cevid.datadroid.lib.sync.Handle;
import it.unive.dais.cevid.datadroid.lib.sync.RefCountedProgressBar;

/**
 * Created by spano on 20/12/2017.
 */
public class ProgressBarParser<Data, P extends AsyncParser<Data, Integer>> implements AsyncParser<Data, Integer> {

    @NonNull
    private final RefCountedProgressBar sharedProgressBar;
    @Nullable
    private Handle handle;
    @NonNull
    private final P parser;

    public ProgressBarParser(@NonNull P parser, @NonNull RefCountedProgressBar sharedProgressBar) {
        this.parser = parser;
        this.sharedProgressBar = sharedProgressBar;
    }

    @NonNull
    @Override
    public AsyncTask<Void, Integer, List<Data>> getAsyncTask() {
        return parser.getAsyncTask();
    }

    @NonNull
    @Override
    public List<Data> parse() throws IOException {
        return parser.parse();
    }

    @NonNull
    @Override
    public String getName() {
        return parser.getName();
    }

    @Override
    public void onPreExecute() {
        handle = sharedProgressBar.acquire();
        parser.onPreExecute();
    }

    @Override
    public void onProgressUpdate(@NonNull Integer p) {
        parser.onProgressUpdate(p);
    }

    @Override
    public void onItemParsed(@NonNull Data d) {
        parser.onItemParsed(d);
    }

    @Override
    public void onPostExecute(@NonNull List<Data> r) {
        parser.onPostExecute(r);
        if (handle != null) handle.release();
    }

    @Override
    public void publishProgress(Integer prog) {
        parser.publishProgress(prog);
    }
}
