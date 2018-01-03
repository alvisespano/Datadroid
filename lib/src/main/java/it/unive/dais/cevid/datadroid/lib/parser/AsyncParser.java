package it.unive.dais.cevid.datadroid.lib.parser;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.List;

import it.unive.dais.cevid.datadroid.lib.util.PercentProgress;

/**
 * Interfaccia che rappresenta un parser asincrono.
 */
public interface AsyncParser<Data> extends Parser<Data> {
    @NonNull AsyncTask<Void, PercentProgress, List<Data>> getAsyncTask();
    void onPreExecute();
    void onProgressUpdate(@NonNull Integer p);
    void onItemParsed(@NonNull Data d);
    void onPostExecute(@NonNull List<Data> r);
    void publishProgress(Integer p);
}
