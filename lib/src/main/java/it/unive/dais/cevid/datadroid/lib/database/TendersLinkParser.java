package it.unive.dais.cevid.datadroid.lib.database;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.Serializable;

import it.unive.dais.cevid.datadroid.lib.parser.AbstractAsyncCsvParser;
import it.unive.dais.cevid.datadroid.lib.parser.ParserException;
import it.unive.dais.cevid.datadroid.lib.parser.progress.ProgressBarManager;

/**
 * Created by gianmarcocallegher on 13/03/2018.
 */

public class TendersLinkParser extends AbstractAsyncCsvParser<TendersLinkParser.Data> {
    public TendersLinkParser(@NonNull Reader rd, boolean hasActualHeader, @NonNull String sep, @Nullable ProgressBarManager pbm) {
        super(rd, hasActualHeader, sep, pbm);
    }

    public TendersLinkParser(@NonNull Reader rd, boolean hasActualHeader, @NonNull String sep) {
        super(rd, hasActualHeader, sep, null);
    }

    public TendersLinkParser(@NonNull File file, boolean hasActualHeader, @NonNull String sep, @Nullable ProgressBarManager pbm) throws FileNotFoundException {
        super(file, hasActualHeader, sep, pbm);
    }

    @NonNull
    @Override
    protected TendersLinkParser.Data parseColumns(@NonNull String[] columns) throws ParserException {
        TendersLinkParser.Data d = new TendersLinkParser.Data();
        d.url = columns[0];
        d.codiceEnte = columns[1];
        return d;
    }

    public static class Data implements Serializable {
        public String url;
        public String codiceEnte;
    }
}

