package it.unive.dais.cevid.aac.parser;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.Serializable;
import java.text.ParseException;
import java.util.List;

import it.unive.dais.cevid.aac.util.AppCompatActivityWithProgressBar;
import it.unive.dais.cevid.aac.util.AsyncTaskWithProgressBar;
import it.unive.dais.cevid.datadroid.lib.parser.AbstractAsyncCsvParser;

/**
 * Created by gianmarcocallegher on 05/12/17.
 */

public class EnrolledParser extends AbstractAsyncCsvParser<EnrolledParser.Data> implements AsyncTaskWithProgressBar{

    private AppCompatActivityWithProgressBar caller;

    public EnrolledParser(@NonNull File file, boolean hasActualHeader, @NonNull String sep, AppCompatActivityWithProgressBar caller) throws FileNotFoundException {
        super(file, hasActualHeader, sep);
        this.caller = caller;
    }

    public EnrolledParser(@NonNull Reader rd, boolean hasActualHeader, @NonNull String sep,AppCompatActivityWithProgressBar caller) {
        super(rd, hasActualHeader, sep);
        this.caller = caller;
    }

    @NonNull
    @Override
    protected Data parseColumns(@NonNull String[] columns) throws ParseException {
        Data d = new Data();
        int displacement = 0;
        // in 2016-20017_iscritti.csv c'è il campo Data che bisogna saltare
        // usiamo un displacement per questo.
        d.anno_accademico = columns[0];
        if(columns.length > 13) displacement = 1;
        d.codice_ateneo = columns[1 + displacement];
        d.nome_ateneo = columns[2 + displacement];
        d.regione_ateneo = columns[3 + displacement];
        d.tipologia_corso = columns[4 + displacement];
        d.gruppo_corso = columns[5 + displacement];
        d.numero_gruppo_classe = columns[6 + displacement];
        d.numero_gruppo_classe = columns[6 + displacement];
        d.nome_gruppo_classe = columns[7 + displacement];
        d.corso_di_studio = columns[8 + displacement];
        d.sede_corso_provincia = columns[9 + displacement];
        d.sede_corso_regione = columns[10 + displacement];
        d.numero_immatricolati = columns[11 + displacement];
        d.numero_studenti = columns[12 + displacement];

        return d;
    }

    @Override
    public void setCallerActivity(AppCompatActivityWithProgressBar caller) {
        this.caller = caller;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        caller.requestProgressBar(this);
    }

    @Override
    protected void onPostExecute(@NonNull List<Data> r) {
        super.onPostExecute(r);
        caller.releaseProgressBar(this);
    }

    public static class Data implements Serializable {
        public String anno_accademico,
        //data,
        codice_ateneo,
        nome_ateneo,
        regione_ateneo,
        tipologia_corso,
        gruppo_corso,
        numero_gruppo_classe,
        nome_gruppo_classe,
        corso_di_studio,
        sede_corso_provincia,
        sede_corso_regione,
        numero_immatricolati,
        numero_studenti;
    }
}
