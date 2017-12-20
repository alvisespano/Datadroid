package it.unive.dais.cevid.datadroid.lib.parser;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ProgressBar;

import java.io.IOException;
import java.util.List;

import it.unive.dais.cevid.datadroid.lib.util.Shared;
import it.unive.dais.cevid.datadroid.lib.util.SharedProgressBar;

/**
 * Created by spano on 20/12/2017.
 */
public class SharedProgressBarParser<Data, Progress, P extends AsyncParser<Data, Progress>>
        implements AsyncParser<Data, Progress> {

    @NonNull
    private final SharedProgressBar sharedProgressBar;
    @Nullable
    private Shared<ProgressBar>.Handle handle;
    @NonNull
    private final P parser;

    public SharedProgressBarParser(@NonNull P parser, @NonNull SharedProgressBar sharedProgressBar) {
        this.parser = parser;
        this.sharedProgressBar = sharedProgressBar;
    }

    @NonNull
    @Override
    public AsyncTask<Void, Progress, List<Data>> getAsyncTask() {
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
    public void onProgressUpdate(@NonNull Progress p) {
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
    public void publishProgress(Progress prog) {
        parser.publishProgress(prog);
    }
}
