package it.unive.dais.cevid.datadroid.lib.util;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import it.unive.dais.cevid.datadroid.lib.util.Function;

public class DataManipulation {

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

    public static <T> void filterByCode(@NonNull List<T> l, int code, @NonNull Function<T, Integer> getCode) {
        filter(l, x -> getCode.apply(x) == code);
    }


    public static <T> void filterByWords(@NonNull List<T> l, @NonNull Collection<String> ss, @NonNull Function<T, String> getText, boolean isCaseSenstive) {
        filter(l, x -> {
            String s0 = getText.apply(x);
            if (isCaseSenstive) s0 = s0.toLowerCase();
            for (String s : ss) {
                if (s0.contains(isCaseSenstive ? s : s.toLowerCase())) return true;
            }
            return false;
        });
    }

    public static <T> void filterByWords(@NonNull List<T> l, @NonNull String[] ss, @NonNull Function<T, String> getText, boolean isCaseSensitive) {
        filterByWords(l, Arrays.asList(ss), getText, isCaseSensitive);
    }


}
