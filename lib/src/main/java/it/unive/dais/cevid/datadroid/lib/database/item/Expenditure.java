package it.unive.dais.cevid.datadroid.lib.database.item;

public class Expenditure {
    private String codiceSiope;
    private String codiceEnte;

    private int anno;
    private double importo;

    private String descrizioneCodice;

    public Expenditure(String codiceSiope, String codiceEnte, int anno, String descrizioneCodice, double importo) {
        this.codiceSiope = codiceSiope;
        this.codiceEnte = codiceEnte;
        this.anno = anno;
        this.descrizioneCodice = descrizioneCodice;
        this.importo = importo;
    }

    public String getCodiceSiope() {
        return codiceSiope;
    }

    public String getCodiceEnte() {
        return codiceEnte;
    }

    public int getAnno() {
        return anno;
    }

    public String getDescrizioneCodice() {
        return descrizioneCodice;
    }

    public double getImporto() {
        return importo;
    }

    public boolean isOfYear(int year) {
        return getAnno() == year;
    }
}
