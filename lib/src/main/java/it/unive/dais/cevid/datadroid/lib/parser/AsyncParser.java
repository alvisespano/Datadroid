package it.unive.dais.cevid.datadroid.lib.parser;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Interfaccia che rappresenta un parser asincrono.
 * @param <Data>
 * @param <Progress>
 */
public interface AsyncParser<Data, Progress> extends Parser<Data> {
    @NonNull AsyncTask<Void, Progress, List<Data>> getAsyncTask();
    void onPreExecute();
    void onProgressUpdate(@NonNull Progress p);
    void onItemParsed(@NonNull Data d);
    void onPostExecute(@NonNull List<Data> r);
    void publishProgress(Progress p);
}
