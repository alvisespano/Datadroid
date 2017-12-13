package it.unive.dais.cevid.aac.parser;

/**
 * Created by fbusolin on 15/11/17.
 */

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.unive.dais.cevid.aac.util.AppCompatActivityWithProgressBar;
import it.unive.dais.cevid.aac.util.AsyncTaskWithProgressBar;
import it.unive.dais.cevid.datadroid.lib.parser.AbstractAsyncParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.unive.dais.cevid.datadroid.lib.util.ProgressStepper;

/**
 * @author fbusolin
 */
public class ParticipantParser extends AbstractAsyncParser<ParticipantParser.Data, ProgressStepper> implements AsyncTaskWithProgressBar {
    public static final String TAG = "ParticipantParser";
    private AppCompatActivityWithProgressBar caller;
    private final String iva;
    private static String single = "%27";
    private static String pair = "%22";
    private static String space = "%20";
    private static String res2015 = "f2fcfe51-fe2b-4e5b-a730-659e24aa2b1d";
    private static String res2016 = "996e869f-d3a3-4938-bd87-38d8d688860a";
    private static String res2017 = "1ffc9410-9d28-47c6-b729-efc4de4e3287";

    public ParticipantParser(String iva) {
        this.iva = iva;

    }

    @NonNull
    public List<Data> parse() throws IOException {
        OkHttpClient client = new OkHttpClient();
        List returnList = new ArrayList();
        Request request2015 = new Request.Builder()
                .url(this.buildURL(res2015))
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Accept", "Application/json")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .build();
        Request request2016 = new Request.Builder()
                .url(this.buildURL(res2016))
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Accept", "Application/json")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .build();
        Request request2017 = new Request.Builder()
                .url(this.buildURL(res2017))
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Accept", "Application/json")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .build();
        try {
            returnList.addAll(parseJSON(client.newCall(request2015).execute().body().string()));
            returnList.addAll(parseJSON(client.newCall(request2016).execute().body().string()));
            returnList.addAll(parseJSON(client.newCall(request2017).execute().body().string()));
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return returnList;
        }
    }

    private String buildURL(String resource) {
        return "http://dati.consip.it/api/action/datastore_search_sql?"
                + "sql=SELECT%20*%20"
                + "FROM" + space + pair + resource + pair + space
                + "WHERE%20%22Partita_Iva%22LIKE%20" + single + iva + single;

        /*SELECT *
        * FROM ...
        * WHERE "Partita_Iva" LIKE '[iva]'*/
    }

    private List<Data> parseJSON(String string) throws JSONException {
        List r = new ArrayList();
        JSONObject jo = new JSONObject(string);
        JSONObject result = jo.getJSONObject("result");
        JSONArray array = result.getJSONArray("records");
        ProgressStepper prog = new ProgressStepper(array.length());
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            Data d = new Data();
            d.esito = obj.optString("Esito_Partecipazione");
            d.forma_partecipazione = obj.optString("Forma_Partecipazione");
            d.data_aggiudicazione = obj.optString("Data_Aggiudicazione/Abilitazione");
            d.id_lotto = obj.optString("Identificativo_Lotto");
            d.full_text = obj.optString("_full_text");
            d.nome_iniziativa = obj.optString("#Denominazione_Iniziativa");
            d.tipo_strumento = obj.optString("Tipo_Strumento");
            d.rag_sociale = obj.optString("Ragione_Sociale");
            d.prog_partecipante = obj.optString("Progressivo_Partecipante");
            d.nome_partecipante = obj.optString("Denominazione_Partecipazione");
            d.nome_lotto = obj.optString("Denominazione_Lotto");
            d.piva = obj.optString("Partita_Iva");
            d.capogruppo = obj.optString("Flag_Capogruppo");
            d.id_download = obj.optString("id");

            r.add(d);
            prog.step();
            publishProgress(prog);
        }
        return r;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        caller.requestProgressBar(this);
    }

    @Override
    protected void onPostExecute(@NonNull List<ParticipantParser.Data> r) {
        super.onPostExecute(r);
        caller.releaseProgressBar(this);
    }

    @Override
    public void setCallerActivity(AppCompatActivityWithProgressBar caller) {
        this.caller = caller;
    }

    public static class Data implements Serializable {
        public String esito,
                forma_partecipazione,
                data_aggiudicazione,
                id_lotto,
                full_text,
                nome_iniziativa,
                tipo_strumento,
                rag_sociale,
                prog_partecipante,
                nome_partecipante,
                nome_lotto,
                piva,
                capogruppo,
                id_download;

    }

}
