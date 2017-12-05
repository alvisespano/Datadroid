package it.unive.dais.cevid.aac.parser;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.text.ParseException;

import it.unive.dais.cevid.datadroid.lib.parser.AbstractAsyncCsvParser;

/**
 * Created by gianmarcocallegher on 05/12/17.
 */

public class GraduatesParser extends AbstractAsyncCsvParser<GraduatesParser.Data> {

    public GraduatesParser(@NonNull File file, boolean hasActualHeader, @NonNull String sep) throws FileNotFoundException {
        super(file, hasActualHeader, sep);
    }

    @NonNull
    @Override
    protected Data parseColumns(@NonNull String[] columns) throws ParseException {
        Data d = new Data();

        d.anno_solare = columns[0];
        d.codice_ateneo = columns[1];
        d.nome_ateneo = columns[2];
        d.laureati = "" + (Integer.parseInt(columns[3]) + Integer.parseInt(columns[4]));

        return d;
    }

    public static class Data implements Serializable {
        public String anno_solare;
        public String codice_ateneo;
        public String nome_ateneo;
        public String laureati;
    }
}
