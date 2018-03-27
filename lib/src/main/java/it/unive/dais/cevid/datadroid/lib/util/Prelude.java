package it.unive.dais.cevid.datadroid.lib.util;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Piccola libreria di utilità di vario genere.
 *
 * @author Alvise Spanò, Università Ca' Foscari
 */
public final class Prelude {

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
        for (char c : cs) {
            s = s.replaceAll(c + "$", "").replaceAll("^" + c, "");
        }
        return s;
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
        x = crop(a0, b0, x);
        return (x - a0) * (b0 - a0) / (b1 - a1) + a1;
    }

    // async task quick API
    //

    public static class AsyncTaskResult<T> {
        private T result = null;
        private Exception exn = null;

        public AsyncTaskResult(T x) {
            result = x;
        }

        public AsyncTaskResult(Exception e) {
            exn = e;
        }

        public boolean hasResult() {
            return exn != null;
        }

        @Nullable
        public T getResult() {
            return result;
        }

        @Nullable
        public Exception getException() {
            return exn;
        }
    }


    public static class AsyncTaskHolder<R> {
        public AsyncTaskHolder(AsyncTask<?, ?, AsyncTaskResult<R>> t) {
            super();
        }
    }

    @SuppressWarnings("unchecked")
    @SuppressLint("StaticFieldLeak")
    @NonNull
    public static <T, R> AsyncTaskResult<R> runOnAsyncTask(@NonNull Function<T, R> f, @Nullable T x) {
        AsyncTask<T, ?, AsyncTaskResult<R>> t = (new AsyncTask<T, Void, AsyncTaskResult<R>>() {
            @Override
            protected AsyncTaskResult<R> doInBackground(T... x) {
                try {
                    return new AsyncTaskResult<R>(f.apply(x[0]));
                } catch (Exception e) {
                    return new AsyncTaskResult<R>(e);
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, x);
        return new AsyncTaskHolder<R>(t);
    }

    public static <R> AsyncTask<Void, ?, R> runOnAsyncTask(@NonNull Function<Void, R> f) {
        return runOnAsyncTask(f, null);
    }

    public static AsyncTask<Void, ?, Void> runOnAsyncTask(Runnable r) {
        return runOnAsyncTask(x -> {
            r.run();
            return null;
        });
    }

    // higher-order functional utilities
    //

    public static <T> void filter(@NonNull List<T> l, @NonNull Function<T, Boolean> f) {
        Collection<T> c = new ArrayList<>();
        for (T x : l) {
            if (f.apply(x)) c.add(x);
        }
        l.retainAll(c);
    }

    public static <T> double sumBy(@NonNull List<T> l, @NonNull Function<T, Double> f) {
        double r = 0.;
        for (T x : l) {
            r += f.apply(x);
        }
        return r;
    }

    public static <T> void filterByCode(@NonNull List<T> l, final int code, @NonNull final Function<T, Integer> getCode) {
        filter(l, new Function<T, Boolean>() {
            @Override
            public Boolean apply(T x) {
                return getCode.apply(x) == code;
            }
        });
    }


    public static <T> void filterByWords(@NonNull List<T> l, @NonNull final Collection<String> ss, @NonNull final Function<T, String> getText, final boolean isCaseSenstive) {
        filter(l, new Function<T, Boolean>() {
            @Override
            public Boolean apply(T x) {
                String s0 = getText.apply(x);
                if (isCaseSenstive) s0 = s0.toLowerCase();
                for (String s : ss) {
                    if (s0.contains(isCaseSenstive ? s : s.toLowerCase())) return true;
                }
                return false;
            }
        });
    }

    public static <T> void filterByWords(@NonNull List<T> l, @NonNull String[] ss, @NonNull Function<T, String> getText, boolean isCaseSensitive) {
        filterByWords(l, Arrays.asList(ss), getText, isCaseSensitive);
    }


}
