package it.unive.dais.cevid.datadroid.lib.database.item;

public class Appalto {
    private String cig;
    private String oggetto;
    private String sceltaContraente;
    private String codice_fiscale_aggiudicatario;
    private String aggiudicatario;
    private double importo;
    private String codiceEnte;

    public Appalto(String cig, String oggetto, String sceltaContraente, String codice_fiscale_aggiudicatario, String aggiudicatario, double importo, String codiceEnte) {
        this.cig = cig;
        this.oggetto = oggetto;
        this.sceltaContraente = sceltaContraente;
        this.codice_fiscale_aggiudicatario = codice_fiscale_aggiudicatario;
        this.aggiudicatario = aggiudicatario;
        this.importo = importo;
        this.codiceEnte = codiceEnte;
    }

    public String getCig() {
        return cig;
    }

    public String getOggetto() {
        return oggetto;
    }

    public String getCodice_fiscale_aggiudicatario() {
        return codice_fiscale_aggiudicatario;
    }

    public String getSceltaContraente() {
        return sceltaContraente;
    }

    public String getAggiudicatario() {
        return aggiudicatario;
    }

    public double getImporto() {
        return importo;
    }

    public String getCodiceEnte() {
        return codiceEnte;
    }

}
