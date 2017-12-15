package it.unive.dais.cevid.aac.parser;

import android.support.annotation.NonNull;
import android.util.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.unive.dais.cevid.aac.util.AppCompatActivityWithProgressBar;
import it.unive.dais.cevid.aac.util.AsyncTaskWithProgressBar;
import it.unive.dais.cevid.datadroid.lib.parser.AbstractAsyncJsonParser;
import it.unive.dais.cevid.datadroid.lib.parser.AbstractAsyncParser;
import it.unive.dais.cevid.datadroid.lib.util.ProgressStepper;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by fbusolin on 23/11/17.
 */

public class MunicipalityParser extends AbstractAsyncJsonParser<MunicipalityParser.Data, ProgressStepper> implements AsyncTaskWithProgressBar {
    private AppCompatActivityWithProgressBar caller;

    public MunicipalityParser(@NonNull Reader rd) {
        super(rd);
    }

    @NonNull
    @Override
    protected Data parseItem(JsonReader reader) throws IOException {
        Data data = new Data();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "id":
                    data.id = reader.nextString();
                    break;
                case "id_regione":
                    data.id_regione = reader.nextString();
                    break;
                case "id_provincia":
                    data.id_provincia = reader.nextString();
                    break;
                case "nome":
                    data.nome = reader.nextString();
                    break;
                case "capoluogo_provincia":
                    data.capoluogo_provincia = reader.nextBoolean();
                    break;
                case "codice_catastale":
                    data.codice_catastale = reader.nextString();
                    break;
                case "latitudine":
                    data.latitudine = reader.nextDouble();
                    break;
                case "longitudine":
                    data.longitudine = reader.nextDouble();
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
        return data;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        caller.requestProgressBar(this);
    }

    @Override
    protected void onPostExecute(@NonNull List<MunicipalityParser.Data> r) {
        super.onPostExecute(r);
        caller.releaseProgressBar(this);
    }

    @Override
    public void setCallerActivity(AppCompatActivityWithProgressBar caller) {
        this.caller = caller;
    }

    public static class Data implements Serializable {
        public String id, id_regione, id_provincia, nome, codice_catastale;
        boolean capoluogo_provincia;
        Double latitudine, longitudine;
    }
}
