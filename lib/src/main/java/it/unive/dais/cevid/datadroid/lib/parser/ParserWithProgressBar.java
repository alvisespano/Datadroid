package it.unive.dais.cevid.datadroid.lib.parser;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ProgressBar;

import java.util.List;

import it.unive.dais.cevid.datadroid.lib.sync.Handle;
import it.unive.dais.cevid.datadroid.lib.sync.Pool;
import it.unive.dais.cevid.datadroid.lib.util.PercentProgress;

/**
 * Created by spano on 20/12/2017.
 */
public abstract class ParserWithProgressBar<Data, Progress extends PercentProgress>
        extends AbstractAsyncParser<Data, Progress> {

    @NonNull
    private final Pool<ProgressBar> pool;
    @Nullable
    private Handle<ProgressBar> handle;

    protected ParserWithProgressBar(@NonNull Pool<ProgressBar> pool) {
        this.pool = pool;
    }
//    @NonNull
//    private final P parser;

//    public ParserWithProgressBar(@NonNull P parser, @NonNull Pool<ProgressBar> pool) {
//        this.parser = parser;
//        this.pool = pool;
//    }

//    @NonNull
//    @Override
//    public AsyncTask<Void, Progress, List<Data>> getAsyncTask() {
//        return parser.getAsyncTask();
//    }
//
//    @NonNull
//    @Override
//    public List<Data> parse() throws IOException {
//        return parser.parse();
//    }
//
//    @NonNull
//    @Override
//    public String getName() {
//        return parser.getName();
//    }

    @Override
    public void onPreExecute() {
        handle = pool.acquire();
    }

    @Override
    public void onProgressUpdate(@NonNull Progress p) {
        if (handle != null) {
            ProgressBar pb = handle.get();
//            handle.apply(pb -> { pb.setProgress(p.getPercent100()); return null; });
            pb.setProgress(30);
        }
    }

    @Override
    public void onPostExecute(@NonNull List<Data> r) {
        if (handle != null) handle.release();
    }

}
