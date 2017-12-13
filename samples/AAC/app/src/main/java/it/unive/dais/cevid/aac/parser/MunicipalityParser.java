package it.unive.dais.cevid.aac.parser;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import it.unive.dais.cevid.aac.util.AppCompatActivityWithProgressBar;
import it.unive.dais.cevid.aac.util.AsyncTaskWithProgressBar;
import it.unive.dais.cevid.datadroid.lib.parser.AbstractAsyncParser;
import it.unive.dais.cevid.datadroid.lib.util.ProgressStepper;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by fbusolin on 23/11/17.
 */

public class MunicipalityParser extends AbstractAsyncParser<MunicipalityParser.Data, ProgressStepper> implements AsyncTaskWithProgressBar {
    private String nome;
    private AppCompatActivityWithProgressBar caller;
    private static String single = "%27";
    private static String pair = "%22";
    private static String space = "%20";
    private String base = "http://ckan.ancitel.it/api/action/datastore_search_sql?sql=" +
            "SELECT%20*%20" +
            "FROM%20%22c381efe6-f73f-4e20-a825-547241eeb457%22%20" +
            "WHERE";
    private String query;

    public MunicipalityParser(String nome) {
        this.nome = nome;
        this.query = base + space +
                pair + "Comune" + pair + space +
                "LIKE" + space + single + this.nome + single;

        /*SELECT *
        * FROM ...
        * WHERE "Comune" LIKE '[nome]'*/
    }

    @NonNull
    @Override
    public List<Data> parse() throws IOException {
        OkHttpClient client = new OkHttpClient();
        List returnList = new ArrayList();
        Request request = new Request.Builder()
                .url(this.query)
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .addHeader("Accept", "Application/json")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .build();
        try {
            returnList.addAll(parseJSON(client.newCall(request).execute().body().string()));
        } catch (JSONException ex) {
            ex.printStackTrace();
            ;
        } finally {
            return returnList;
        }
    }

    private List<Data> parseJSON(String data) throws JSONException {
        List<Data> list = new ArrayList<>();
        JSONObject jo = new JSONObject(data);
        JSONObject result = jo.getJSONObject("result");
        JSONArray array = result.getJSONArray("records");
        ProgressStepper prog = new ProgressStepper(array.length());
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            Data d = new Data();
            d.id = obj.optString("id");
            d.montano = obj.optString("IndiceMontanita");
            d.provincia = obj.optString("Provincia");
            d.regione = obj.optString("Regione");
            d.comune = obj.optString("Comune");
            d.classe = obj.optString("ClasseComune");
            d.tipo = obj.optString("TipoComune");
            d.superficie = obj.optString("SuperficieKmq");
            d.urbanizzazione = obj.optString("GradoUrbaniz");
            d.min_altezza = obj.optString("AltezzaMinima");
            d.zona_altimetrica = obj.optString("ZonaAltimetrica");
            d.altezza_centro = obj.optString("AltezzaCentro");
            d.ISTAT = obj.optString("ISTAT");
            d.lat = obj.optString("Latitudine");
            d.lng = obj.optString("Longitudine");
            d.area_geografica = obj.optString("AreaGeo");
            d.zona_clima = obj.optString("ZonaClimatica");
            d.zona_sismica = obj.optString("ZonaSismica");
            d.pop_straniera = obj.optString("PopStraniera");
            d.popolazione_residente = obj.optString("PopResidente");
            d.densita = obj.optString("DensitaDemografica");
            d.altezza_massima = obj.optString("AltezzaMassima");
            d.sigla_provincia = obj.optString("SiglaProv");
            list.add(d);
            prog.step();
            publishProgress(prog);
        }

        return list;
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
        public String montano,
                provincia,
                regione,
                comune,
                classe,
                tipo,
                superficie,
                urbanizzazione,
                min_altezza,
                zona_altimetrica,
                altezza_centro,
                ISTAT,
                lat,
                lng,
                area_geografica,
                zona_sismica,
                pop_straniera,
                popolazione_residente,
                densita,
                altezza_massima,
                sigla_provincia,
                zona_clima,
                id;

    }
}
