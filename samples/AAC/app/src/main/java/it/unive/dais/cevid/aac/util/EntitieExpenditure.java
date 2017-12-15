package it.unive.dais.cevid.aac.util;

import java.io.Serializable;

import it.unive.dais.cevid.datadroid.lib.parser.SoldipubbliciParser;

/**
 * Created by gianmarcocallegher on 14/12/17.
 */

public class EntitieExpenditure implements Serializable {

    private String descrizione_codice, codice_siope, importo;

    public EntitieExpenditure (SoldipubbliciParser.Data x, String anno) {
        descrizione_codice = x.descrizione_codice;
        codice_siope = x.codice_siope;
        switch (anno) {
            case "2013":
                importo = x.importo_2013;
                break;
            case "2014":
                importo = x.importo_2014;
                break;
            case "2015":
                importo = x.importo_2015;
                break;
            case "2016":
                importo = x.importo_2016;
                break;
            case "2017":
                importo = x.importo_2017;
                break;
            default:
                importo = "0";
        }
    }

    public String getDescrizione_codice() {
        return descrizione_codice;
    }

    public String getCodice_siope() {
        return codice_siope;
    }

    public String getImporto() {
        return importo;
    }
}
