package it.unive.dais.cevid.datadroid.template;

import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.coreutils.BuildConfig;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


/**
 * Activity per la schermata di crediti e about.
 *
 * @author Alvise Spanò, Università Ca' Foscari
 */
public class AboutActivity extends AppCompatActivity {
    /**
     * Metodo di creazione dell'activity che imposta il layout e la text view con la stringa con i crediti.
     * @param saveInstanceState
     */
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_about);
        TextView tv_1 = (TextView) findViewById(R.id.textView_1);
        tv_1.setText(getCredits());
    }

    /**
     * Produce la stringa completa coi crediti.
     * @return ritorna la stringa completa.
     */
    public String getSystemInfo() {
        ApplicationInfo ai = getApplicationInfo();
        StringBuffer buf = new StringBuffer();
        buf.append("\tVERSION.RELEASE {").append(Build.VERSION.RELEASE).append("}");
        buf.append("\n\tVERSION.INCREMENTAL {").append(Build.VERSION.INCREMENTAL).append("}");
        buf.append("\n\tVERSION.SDK {").append(Build.VERSION.SDK_INT).append("}");
        buf.append("\n\tBOARD {").append(Build.BOARD).append("}");
        buf.append("\n\tBRAND {").append(Build.BRAND).append("}");
        buf.append("\n\tDEVICE {").append(Build.DEVICE).append("}");
        buf.append("\n\tFINGERPRINT {").append(Build.FINGERPRINT).append("}");
        buf.append("\n\tHOST {").append(Build.HOST).append("}");
        buf.append("\n\tID {").append(Build.ID).append("}");
        return String.format(
                "--- APP ---\n" +
                        "%s v%s [%s]\n" +
                        "(c) %s %s @ %s - %s \n\n" +
                        "--- ANDROID ---\n%s",
                getString(ai.labelRes),
                BuildConfig.VERSION_NAME,
                BuildConfig.BUILD_TYPE,
                R.string.credits_year, R.string.credits_project, R.string.credits_company, R.string.credits_authors,
                buf);
    }


    private CharSequence getCredits() {
        return String.format("%s", getString(R.string.credits));
    }

}
