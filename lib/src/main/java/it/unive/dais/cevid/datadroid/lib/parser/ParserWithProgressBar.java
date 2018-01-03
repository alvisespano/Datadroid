package it.unive.dais.cevid.datadroid.lib.parser;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ProgressBar;

import java.io.IOException;
import java.util.List;

import it.unive.dais.cevid.datadroid.lib.sync.Handle;
import it.unive.dais.cevid.datadroid.lib.sync.Pool;
import it.unive.dais.cevid.datadroid.lib.util.PercentProgress;

/**
 * Created by spano on 20/12/2017.
 */
public class ParserWithProgressBar<Data, Progress extends PercentProgress, P extends AsyncParser<Data, Progress>>
        implements AsyncParser<Data, Progress> {

    @NonNull
    private final Pool<ProgressBar> pool;
    @Nullable
    private Handle<ProgressBar> handle;
    @NonNull
    private final P parser;

    public ParserWithProgressBar(@NonNull P parser, @NonNull Pool<ProgressBar> pool) {
        this.parser = parser;
        this.pool = pool;
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
        handle = pool.acquire();
        parser.onPreExecute();
    }

    @Override
    public void onProgressUpdate(@NonNull Progress p) {
        if (handle != null) {
            handle.apply(pb -> { pb.setProgress(p.getPercent100()); return null; });
        }
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
