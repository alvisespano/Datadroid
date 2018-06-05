package it.unive.dais.cevid.datadroid.lib.parser;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import it.unive.dais.cevid.datadroid.lib.progress.ProgressBarManager;
import it.unive.dais.cevid.datadroid.lib.progress.ProgressCounter;
import it.unive.dais.cevid.datadroid.lib.util.Prelude;

/**
 * Classe astratta che rappresenta la superclasse dei parser CSV.
 * Implementa già funzionalità base per la manipolazione di file CSV, richiedendo l'override del solo metodo {@code parseColumns}.
 * Il generic Progress della superclasse {@code AbstractAsyncParser} è Integer: questa classe astratta implementa il conteggio
 * delle linee durante il parsing, permettendo la customizzazione del progresso dell'operazione tramite override del metodo {@code onProgressUpdate},
 * secondo le linee guida di Android per la classe AsyncTask.
 * Ad esempio:
 * <blockquote><pre>
 * {@code
 *  protected void onProgressUpdate(Integer... progress) {
 *      System.println(String.format("parsing line: %d", progress[0]));
 * }
 * }
 * </pre></blockquote>
 * <p>
 * Ancora, il generic Input della superclasse {@code AbstractAsyncParser} è {@code BufferedReader}, permettendo il parsing di lunghi file
 * o stringhe contenenti numerose linee in formato CSV.
 *
 * @param <Data> tipo di una riga di dati.
 * @author Alvise Spanò, Università Ca' Foscari
 */
public abstract class AbstractCsvAsyncParser<Data> extends AbstractAsyncParser<Data, ProgressCounter> {

    protected final boolean hasActualHeader;
    @NonNull
    protected final String sep;
    @NonNull
    protected final BufferedReader reader;
    @Nullable
    protected String[] header = null;

    /**
     * Costruttore tramite parametro di tipo Reader.
     *
     * @param rd              parametro di tipo Reader da cui leggere il CSV.
     * @param hasActualHeader flag booleano che indica se il CSV ha un header alla prima riga.
     * @param sep             separatore tra le colonne del CSV (ad esempio il punto e virgola ";" oppure la virgola ",").
     */
    protected AbstractCsvAsyncParser(@NonNull Reader rd, boolean hasActualHeader, @NonNull String sep, @Nullable ProgressBarManager pbm) {
        super(pbm);
        this.reader = new BufferedReader(rd);
        this.sep = sep;
        this.hasActualHeader = hasActualHeader;
    }

    /**
     * Implementa un parser linea-per-linea con progresso.
     * Se ridefinita da una sottoclasse, si deve occupare non solo del parsing ma anche della gestione
     * dell'header, sia in caso sia presente sia in caso non lo sia.
     *
     * @return lista di FiltrableData.
     * @throws IOException lanciata se il reader fallisce.
     */
    @Override
    @NonNull
    public List<Data> parse() throws IOException {
        if (hasActualHeader()) setHeader(split(reader.readLine()));
        List<Data> r = new ArrayList<>();
        @Nullable String line;
        ProgressCounter prog = new ProgressCounter();
        final String TAG = getName();
        while ((line = reader.readLine()) != null) {
            int linen = prog.getCurrentCounter();
            try {
                if (linen == 0 && !hasActualHeader()) setHeader(line);
                r.add(parserLine(line));
                publishProgress(prog);
            } catch (ParserException e) {
                Log.w(TAG, String.format("recoverable parse error at line %d: %s", linen, e.getLocalizedMessage()));
            }
            prog.stepCounter();
        }
        return r;
    }

    /**
     * Splitta una linea CSV secondo il separatore impostato e rispettando le regole di escaping nelle quotation.
     *
     * @param line la stringa da splittare.
     * @return l'array di stringhe splittate.
     */
    protected String[] split(@NonNull String line) {
        return line.split(String.format("%s(?=([^\"]*\"[^\"]*\")*[^\"]*$)", getSeparator()));
    }

    /**
     * Imposta l'header di default quando non ce n'è uno nel CSV.
     * Dall'implementazione della {@code parse} viene chiamato una volta sola dopo aver parsato la prima linea.
     *
     * @param line stringa che contiene la linea del CSV da cui estrapolare il numero di colonne per generare l'header.
     */
    protected void setHeader(@NonNull String line) {
        String[] hd = split(line);
        for (int j = 0; j < hd.length; ++j) {
            hd[j] = String.valueOf(j);
        }
        setHeader(hd);
    }

    /**
     * Parser di una singola linea.
     * L'implementazione di default invoca {@code parseColumns} passandogli la linea splittata tramite il separatore dato.
     *
     * @param line stringa con la linea da parsare.
     * @return ritorna un singolo oggetto di tipo FiltrableData.
     */
    @NonNull
    protected Data parserLine(@NonNull String line) throws ParserException {
        return onItemParsed(parseColumns(split(line)));
    }

    /**
     * Questo metodo deve essere implementato nelle sottoclassi.
     *
     * @param columns array di stringhe che contiene ogni colonna di una riga del CSV.
     * @return ritorna un singolo oggetto di tipo FiltrableData.
     */
    @NonNull
    protected abstract Data parseColumns(@NonNull String[] columns) throws ParserException;

    /**
     * Getter del separatore.
     *
     * @return ritorna il separatore.
     */
    @NonNull
    public String getSeparator() {
        return sep;
    }

    /**
     * Getter dell'header.
     * Questo metodo ritorna sempre un header, generato di default oppure parsato dal CSV; può ritornare
     * {@code null} prima che il parsing abbia luogo, ma non significa che l'header non c'è.
     * Un header c'è sempre - generato oppure no.
     *
     * @return ritorna l'header. Se {@code null} significa che non è ancora stato parsato o impostato.
     */
    @Nullable
    public String[] getHeader() {
        return header;
    }

    /**
     * Forza un header per questo parser CSV.
     * Cambiare header può sollevare l'eccezione {@code IllegalArgumentException} se la lunghezza dell'header precedente è diversa da quella del
     * nuovo header.
     *
     * @param columns array di stringhe con i nomi delle colonne.
     */
    public void setHeader(@NonNull String[] columns) {
        trimStrings(columns);
        if (header != null && columns.length != header.length)
            throw new IllegalArgumentException(String.format("CSV header size mismatch: former header has %d columns while new header has %d", header.length, columns.length));
        header = columns;
    }

    /**
     * Ritorna true se il CSV ha un header altrimento false.
     * Da non confondere con un header nullo, che invece significa che non è stato ancora parsato.
     * Il metodo {@code getHeader} ritorna sempre un header - generato automaticamente oppure realmente presente nel CSV - ma questo metodo
     * ritorna true solamente se tale header è presente nel CSV.
     *
     * @return true se il CSV ha un header come prima linea; false altrimenti.
     */
    public boolean hasActualHeader() {
        return hasActualHeader;
    }

    /**
     * Esegui il trim di ogni stringa dell'array dato.
     * Vengono eliminati i caratteri di spazio e le virgolette.
     *
     * @param ss l'array di stringhe.
     */
    protected String[] trimStrings(String[] ss) {
        String[] r = new String[ss.length];
        for (int i = 0; i < ss.length; ++i) {
            r[i] = trimString(ss[i]);
        }
        return r;
    }

    /**
     * Esegui il trim di una stringa.
     * Vengono eliminati i caratteri di spazio e le virgolette.
     *
     * @param s la stringa.
     */
    protected static String trimString(String s) {
        return Prelude.trim(s, new char[]{' ', '"', '\''});
    }
}


