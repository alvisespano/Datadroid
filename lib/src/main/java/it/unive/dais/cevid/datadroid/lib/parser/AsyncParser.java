package it.unive.dais.cevid.datadroid.lib.parser;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import java.util.List;

import it.unive.dais.cevid.datadroid.lib.progress.ProgressCounter;

/**
 * Interfaccia che rappresenta un parser asincrono.
 */
public interface AsyncParser<Data, P extends ProgressCounter> extends Parser<Data> {
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

    /**
     * Invocato per ogni linea o riga o, in generale, elemento di tipo Data.
     * IMPORTANTE: l'invocazione di questo metodo non è garantita. Ogni sottoclasse è responsabile dell'invocazione.
     * @param x il dato singolo appena parsato.
     * @return il nuovo dato singolo, eventualmente riprocessato.
     */
    @WorkerThread
    Data onItemParsed(@NonNull Data x);
}
