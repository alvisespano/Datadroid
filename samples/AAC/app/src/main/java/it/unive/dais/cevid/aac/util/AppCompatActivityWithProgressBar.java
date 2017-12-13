package it.unive.dais.cevid.aac.util;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 13/12/17.
 */

public abstract class AppCompatActivityWithProgressBar extends AppCompatActivity{
    protected ProgressBar progressBar;
    private List<AsyncTaskWithProgressBar> tasks = new ArrayList<>();
    public abstract void setProgressBar();
    public ProgressBar getProgressBar(){
        return progressBar;
    };
    public void requestProgressBar(AsyncTaskWithProgressBar requester){
        if(this.progressBar.getVisibility() != View.VISIBLE){
            progressBar.setVisibility(View.VISIBLE);
        }
        this.tasks.add(requester);
    }
    public void releaseProgressBar(AsyncTaskWithProgressBar requester){
        this.tasks.remove(requester);
        if(this.progressBar.getVisibility() != View.GONE){
            this.progressBar.setVisibility(View.GONE);
        }
    };
}
