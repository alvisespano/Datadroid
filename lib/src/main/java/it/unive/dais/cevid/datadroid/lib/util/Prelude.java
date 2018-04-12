package it.unive.dais.cevid.datadroid.lib.util;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Raccolta di utilità di vario genere.
 *
 * @author Alvise Spanò, Università Ca' Foscari
 */
public final class Prelude {

    private Prelude() {
    }    // dummy constructor

    // misc stuff
    //

    /**
     * Elimina tutte le occorrenze dei caratteri passati dall'inizio e dalla fine della stringa data.
     * Non vengono eliminate le occorrenze in mezzo alla stringa.
     * Questa funzione produce una nuova stringa, non modifica quella passata come argomento.
     *
     * @param s  la stringa da manipolare.
     * @param cs array di caratteri da eliminare.
     * @return ritorna la nuova stringa.
     */
    public static String trim(String s, char[] cs) {
        String s1 = s;
        for (char c : cs) {
            s1 = s1.replaceAll(c + "$", "").replaceAll("^" + c, "");
        }
        return s1;
    }

    /**
     * Limita il parametro content all'interno dell'intervallo tra a e b dato un oggetto comparatore non-nullo.
     * Questa funzione è generica sul tipo numerico e richiede un Comparator per eseguire i confronti tra i valori.
     *
     * @param a   estremo sinistro dell'intervallo.
     * @param b   estremo destro dell'intervallo.
     * @param x   valore da limitare.
     * @param c   oggetto di tipo {@code Comparator<T>} che permette il confronto tra i valori di tipo T.
     * @param <T> il tipo dei valori numerici da manipolare.
     * @return risultato.
     */
    public static <T> T crop(T a, T b, T x, @NonNull Comparator<T> c) {
        return c.compare(x, a) <= 0 ? a : c.compare(x, b) >= 0 ? b : x;
    }

    /**
     * Limita il parametro content all'interno dell'intervallo tra a e b.
     * Questa funzione è generica sul tipo numerico, richiedento che implementi l'interfaccia {@code Comparable<T>}.
     * Ad esempio è possibile chiamare questa funzione con gli int, i double ed altri tipi builtin.
     *
     * @param a   estremo sinistro dell'intervallo.
     * @param b   estremo destro dell'intervallo.
     * @param x   valore da limitare.
     * @param <T> il tipo dei valori numerici da manipolare, che deve implementare {@code Comparable<T>}.
     * @return risultato.
     */
    public static <T extends Comparable<T>> T crop(T a, T b, T x) {
        return x.compareTo(a) <= 0 ? a : x.compareTo(b) >= 0 ? b : x;
    }

    /**
     * Dato un parametro content definito nell'intervallo tra a0 e b0, proietta content entro l'intervallo tra a1 e b1 mantenendo le proporzioni.
     * Chiamato y il risultato della proiezione, garantisce che {@code (content - a0) / (b0 - a0) = (y - a1) / (b1 - a1)}.
     * In altre parole, garantisce che la distanza
     * relativa di content dall'estremo sinistro dell'intervallo di partenza sta alla lunghezza di quest'ultimo come la distanza di
     * y dall'estremo sinistro dell'intervallo di destinazione sta alla lunghezza di quest'ultimo.
     * Per esempio, {@code proj(0, 10, 100, 200, 6) = 160} poiché la proporzione tra 6 e l'intervallo {@code [0, 10]} è uguale
     * alla proporzione tra 160 e {@code [100, 200]}.
     *
     * @param a0 estremo sinistro dell'intervallo di partenza.
     * @param b0 estremo destro dell'intervallo di partenza.
     * @param a1 estremo sinistro dell'intervallo di destinazione.
     * @param b1 estremo destro dell'intervallo di destinazione.
     * @param x  valore di tipo double da proiettare.
     * @return risultato della proiezione.
     */
    public static double proj(double a0, double b0, double a1, double b1, double x) {
        double crop = crop(a0, b0, x);
        return (crop - a0) * (b0 - a0) / (b1 - a1) + a1;
    }

    /**
     * Converte una URL in un {@code InputStreamReader}.
     * Questo metodo statico è utile per implementare, nelle sottoclassi di questa classe, un costruttore aggiuntivo un parametro di
     * tipo URL come, che può essere convertito in un {@code InputStreamReader} tramite questo metodo statico e passato rapidamente
     * al costruttore principale, come per esempio:
     * <blockquote><pre>
     * {@code
     * public static class MyDataParser extends AbstractAsyncParser<MapItem, Void, InputStreamReader> {
     *      protected MyDataParser(InputStreamReader rd) {
     *          super(rd);
     *      }
     * <p>
     *      protected MyDataParser(URL url) throws IOException {
     *          super(urlToReader(url));
     *      }
     * <p>
     *      protected List<MapItem> parse(InputStreamReader rd) throws IOException {
     *          // fai qualcosa usando rd
     *      }
     * }
     * }
     * </pre></blockquote>
     *
     * @param url parametro di tipo URL.
     * @return risultato di tipo InputStreamReader.
     * @throws IOException lancia questa eccezione quando sorgono problemi di I/O.
     */
    @NonNull
    @WorkerThread
    public static InputStreamReader urlToReader(@NonNull URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        InputStream stream = connection.getInputStream();
        return new InputStreamReader(stream);
    }


    // async task quick wrappers
    //

    public static class AsyncTaskResult<R> {

        protected final AsyncTask<?, ?, Result<R>> task;

        public AsyncTaskResult(AsyncTask<?, ?, Result<R>> t) {
            this.task = t;
        }

        public Result<R> get() throws ExecutionException, InterruptedException {
            return task.get();
        }

        public boolean hasResult() throws ExecutionException, InterruptedException {
            return get().hasResult();
        }

        @Nullable
        public R getResult() throws ExecutionException, InterruptedException {
            return get().getResult();
        }

        @Nullable
        public Exception getException() throws ExecutionException, InterruptedException {
            return get().getException();
        }

        static class Result<R> {

            @Nullable
            private R result;
            @Nullable
            private Exception exn;

            Result(@Nullable R x) {
                result = x;
                exn = null;
            }

            Result(@NonNull Exception e) {
                exn = e;
                result = null;
            }

            public boolean hasResult() {
                return exn != null;
            }

            @Nullable
            public R getResult() {
                return result;
            }

            @NonNull
            public Exception getException() {
                if (exn != null) return exn;
                else throw new RuntimeException("AsyncTask has result and no exception");
            }
        }
    }

    @SuppressWarnings("unchecked")
    @NonNull
    public static <T, R> AsyncTaskResult<R> runOnAsyncTask(@NonNull Function<T, R> f, @Nullable T x) {
        return new AsyncTaskResult<>(new AsyncTask<T, Void, AsyncTaskResult.Result<R>>() {
            @Override
            protected AsyncTaskResult.Result<R> doInBackground(T... x) {
                try {
                    return new AsyncTaskResult.Result<>(f.apply(x[0]));
                } catch (Exception e) {
                    return new AsyncTaskResult.Result<>(e);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, x));
    }

    public static <R> AsyncTaskResult<R> runOnAsyncTask(@NonNull Function<Void, R> f) {
        return runOnAsyncTask(f, null);
    }

    public static void runOnAsyncTask(Runnable r) {
        runOnAsyncTask(x -> {
            r.run();
            return null;
        });
    }



}
