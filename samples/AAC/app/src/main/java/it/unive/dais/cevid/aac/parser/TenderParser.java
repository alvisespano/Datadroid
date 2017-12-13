package it.unive.dais.cevid.aac.parser;

/**
 * Created by fusolin on 15/11/17.
 */

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.unive.dais.cevid.aac.util.AppCompatActivityWithProgressBar;
import it.unive.dais.cevid.aac.util.AsyncTaskWithProgressBar;
import it.unive.dais.cevid.datadroid.lib.parser.AbstractAsyncParser;
import it.unive.dais.cevid.datadroid.lib.util.ProgressStepper;
import okhttp3.Request;
import okhttp3.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author fbusolin
 */
public class TenderParser extends AbstractAsyncParser<TenderParser.Data, ProgressStepper> implements AsyncTaskWithProgressBar {
    public static final String TAG = "TenderParser";
    private static String single = "%27";
    private static String pair = "%22";
    private static String space = "%20";
    private static String res2015 = "072fac7d-beda-4146-b574-1108e3bc030f";
    private static String res2016 = "5e12248d-07be-4e94-8be7-05b49787427f";
    private static String res2017 = "377784b5-bb11-4a3e-a3a7-e1e48d122892";
    private final String lotto;
    private AppCompatActivityWithProgressBar caller;

    public TenderParser(String lotto) {
        this.lotto = lotto;
    }

    @NonNull
    public List parse() throws IOException {
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
                + "WHERE%20%22Identificativo_Lotto%22LIKE%20" + single + lotto + single;

        /*SELECT *
        * FROM ...
        * WHERE "Identificativo_Lotto" LIKE '[lotto]'*/
    }

    public List<Data> parseJSON(String data) throws JSONException {
        List r = new ArrayList();
        JSONObject jo = new JSONObject(data);
        JSONObject result = jo.getJSONObject("result");
        JSONArray array = result.getJSONArray("records");
        ProgressStepper prog = new ProgressStepper(array.length());
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            Data d = new Data();
            d.unita_misura = obj.optString("Unità_Misura");
            d.per_erosione = obj.optString("Percentuale_Erosione");
            d.crit_aggiudicazione = obj.optString("Criterio_Aggiudicazione");
            d.cat_merceologica = obj.optString("Categoria_Merceologica");
            d.tipo_strumento = obj.optString("Tipo_Strumento");
            d.flag_fee = obj.optString("Flag_Commissione_FEE");
            d.aggiudicazione_esclusiva = obj.optString("Aggiudicazione_Esclusiva");
            d.importo_massimale = obj.optString("Importo_Massimale");
            d.data_fine = obj.optString("Data_Termine");
            d.mod_svolgimento = obj.optString("Modalità_Svolgimento");
            d.data_pubb = obj.optString("Data_Pubblicazione");
            d.quantita_massimale = obj.optString("Quantità_Massimale");
            d.verde = obj.optString("Verde");
            d.denominazione_lotto = obj.optString("Denominazione_Lotto");
            d.tipo_procedura = obj.optString("Tipo_Procedura");
            d.base_asta = obj.optString("Base_Asta");
            d.id_lotto = obj.optString("Identificativo_Lotto");
            d.n_operatori_part = obj.optString("Numero_Operatori_Economici_Partecipanti");
            d.tipo_lotto = obj.optString("Tipologia_Lotto");
            d.denominazione = obj.optString("#Denominazione_Bando");
            d.data_auth_75 = obj.optString("Data_Autorizzazione_7_5");
            d.data_attivazione = obj.optString("Data_Attivazione");
            d.data_auth_65 = obj.optString("Data_Autorizzazione_6_5");
            d.n_operatori_agg = obj.optString("Numero_Operatori_Economici_Aggiudicatari/Abilitati");
            d.data_agg = obj.optString("Data_Aggiudicazione");
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
    protected void onPostExecute(@NonNull List<TenderParser.Data> r) {
        super.onPostExecute(r);
        caller.releaseProgressBar(this);
    }

    @Override
    public void setCallerActivity(AppCompatActivityWithProgressBar caller) {
        this.caller = caller;
    }

    public static class Data implements Serializable {
        public String unita_misura,
                per_erosione,
                crit_aggiudicazione,
                cat_merceologica,
                tipo_strumento,
                flag_fee,
                aggiudicazione_esclusiva,
                importo_massimale,
                data_fine,
                mod_svolgimento,
                data_pubb,
                quantita_massimale,
                verde,
                denominazione_lotto,
                tipo_procedura,
                base_asta,
                id_lotto,
                n_operatori_part,
                tipo_lotto,
                denominazione,
                data_auth_75,
                data_attivazione,
                data_auth_65,
                n_operatori_agg,
                data_agg,
                id_download;

        public String getMassimale() {
            return quantita_massimale.isEmpty() ? importo_massimale : quantita_massimale;
        }
    }


}