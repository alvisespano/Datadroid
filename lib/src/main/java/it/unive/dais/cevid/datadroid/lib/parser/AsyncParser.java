package it.unive.dais.cevid.datadroid.lib.parser;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import java.util.List;

import it.unive.dais.cevid.datadroid.lib.parser.progress.ProgressStepper;

/**
 * Interfaccia che rappresenta un parser asincrono.
 */
public interface AsyncParser<Data, P extends ProgressStepper> extends Parser<Data> {
    @NonNull
    AsyncTask<Void, P, List<Data>> getAsyncTask();

    @UiThread
    void onPreExecute();

    @UiThread
    void onPostExecute(@NonNull List<Data> r);

    @WorkerThread
    void onProgressUpdate(@NonNull P p);

    @WorkerThread
    List<Data> onPostParse(@NonNull List<Data> r);
}
